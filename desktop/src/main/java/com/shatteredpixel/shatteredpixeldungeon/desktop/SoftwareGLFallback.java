/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2025 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.desktop;

import com.badlogic.gdx.utils.SharedLibraryLoader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Some computers cannot give the game a modern OpenGL context: virtual
 * machines, remote sessions, and computers without a graphics card only offer
 * the OpenGL 1.1 software driver that is built into Windows, which is far below
 * the OpenGL 2.0 that libGDX requires. Trying to run the game on those machines
 * ends in a "Couldn't create window" error.
 *
 * For those machines the game is shipped with a software renderer (Mesa's
 * llvmpipe, a normal OpenGL driver that draws on the CPU instead of a graphics
 * card). It is only used if the game was unable to create its window with the
 * system's own OpenGL driver, in which case the game restarts itself with the
 * software renderer enabled. Nothing here is used on machines that have working
 * graphics drivers.
 */
public class SoftwareGLFallback {

	//Set to "software" when the game runs (or is about to run) on the bundled
	//software renderer. It doubles as the guard that stops the game from
	//restarting itself over and over.
	public static final String MODE_PROPERTY = "deranged.glMode";
	public static final String SOFTWARE_MODE = "software";

	//LibGDX/LWJGL pick their OpenGL library through this property. Pointing it at
	//our copy of opengl32.dll makes sure the game and the software renderer match.
	private static final String LWJGL_GL_LIBRARY_PROPERTY = "org.lwjgl.opengl.libname";

	//The renderer as it is bundled inside the game jar. These are the Windows
	//x64 DLLs of the Mesa3D distribution, https://github.com/pal1000/mesa-dist-win
	//(the automated 'Build Windows EXE' workflow puts them there).
	private static final String DLL_RESOURCE_DIR = "software-gl";
	//Loaded in this order, so that dependencies are already in the process by the
	//time the driver itself is loaded. Files that aren't bundled are skipped.
	private static final String[] DLLS = {
			"vcruntime140.dll",
			"vcruntime140_1.dll",
			"msvcp140.dll",
			"libglapi.dll",
			"libgallium_wgl.dll",
			"dxil.dll",
			"opengl32.dll",
	};

	//Where the DLLs are unpacked to. The name is stable so that they only have to
	//be written out once, and the game's own temp folder is always writable.
	private static final String EXTRACT_DIR = "deranged-software-gl";

	private static boolean loaded = false;

	/** True if the game is meant to use the software renderer. */
	public static boolean isSoftwareMode() {
		return SOFTWARE_MODE.equals(System.getProperty(MODE_PROPERTY));
	}

	/** Human readable description of the renderer the game is set up to use. */
	public static String currentMode() {
		return isSoftwareMode() ? "software renderer" : "graphics driver";
	}

	/** True if this build of the game actually has a software renderer to use. */
	public static boolean isAvailable() {
		return SharedLibraryLoader.isWindows && isBundled("opengl32.dll");
	}

	private static boolean isBundled(String fileName) {
		return SoftwareGLFallback.class.getResource("/" + DLL_RESOURCE_DIR + "/" + fileName) != null;
	}

	/**
	 * Unpacks the bundled renderer and loads it into this process. Must run before
	 * anything loads OpenGL, so before the game itself is created.
	 */
	public static void loadSoftwareRenderer() {
		if (loaded) return;
		loaded = true;

		File dir = extract();
		if (dir == null) return;

		for (String fileName : DLLS) {
			File dll = new File(dir, fileName);
			if (!dll.isFile()) continue;
			try {
				System.load(dll.getAbsolutePath());
			} catch (Throwable e) {
				System.err.println("[DERanged] Could not load the bundled software renderer (" + fileName + "): " + e);
				return;
			}
		}

		File gl = new File(dir, "opengl32.dll");
		if (gl.isFile()) {
			System.setProperty(LWJGL_GL_LIBRARY_PROPERTY, gl.getAbsolutePath());
		}
	}

	/**
	 * Called when the game could not start up. If the only problem is that this
	 * computer has no usable OpenGL driver, the game is restarted with the
	 * bundled software renderer, and true is returned. If the failure has another
	 * cause (or the software renderer was already tried), false is returned and
	 * the game reports the error as usual.
	 */
	public static boolean handleStartupFailure(Throwable failure, String[] args) {
		if (isSoftwareMode() || !isAvailable() || !mentionsWindowCreation(failure)) {
			return false;
		}

		System.out.println("[DERanged] This computer's graphics driver could not be used:");
		System.out.println("[DERanged]   " + message(failure));
		System.out.println("[DERanged] Restarting the game on the bundled software renderer, which runs on the CPU and does not need a graphics card.");
		System.out.flush();

		if (!relaunch(args)) {
			return false;
		}

		//the restarted game has already reported any errors of its own
		return true;
	}

	private static boolean mentionsWindowCreation(Throwable failure) {
		int depth = 0;
		for (Throwable current = failure; current != null && depth < 10; current = current.getCause(), depth++) {
			String message = current.getMessage();
			if (message != null) {
				String normalised = message.toLowerCase(Locale.ROOT).replace("'", "").replace("\u2019", "");
				if (normalised.contains("couldnt create window")) {
					return true;
				}
			}
			if (current.getCause() == current) break;
		}
		return false;
	}

	private static String message(Throwable failure) {
		Throwable last = failure;
		int depth = 0;
		while (last.getCause() != null && last.getCause() != last && depth++ < 10) {
			last = last.getCause();
		}
		String message = last.getMessage();
		if (message == null) {
			return last.toString();
		}
		return message;
	}

	/** Restarts the game in a new process, on the software renderer. */
	private static boolean relaunch(String[] args) {
		List<String> command = new ArrayList<String>();
		command.add(javaExecutable());
		command.add("-D" + MODE_PROPERTY + "=" + SOFTWARE_MODE);
		command.add("-cp");
		command.add(System.getProperty("java.class.path"));
		command.add(DesktopLauncher.class.getName());
		command.addAll(Arrays.asList(args));

		try {
			System.out.println("[DERanged] Restarting with: " + command);
			System.out.flush();
			Process process = new ProcessBuilder(command).inheritIO().start();
			process.waitFor();
			return true;
		} catch (Exception e) {
			System.err.println("[DERanged] Could not restart the game on the software renderer: " + e);
			System.err.flush();
			return false;
		}
	}

	private static String javaExecutable() {
		String home = System.getProperty("java.home") + File.separator + "bin" + File.separator;
		String extension = SharedLibraryLoader.isWindows ? ".exe" : "";
		//start without a console window when the game was not started from one,
		//and with one when it was, so that any output stays visible
		String launcher = System.console() == null ? "javaw" : "java";
		File preferred = new File(home + launcher + extension);
		File fallback = new File(home + "java" + extension);
		return preferred.isFile() ? preferred.getAbsolutePath() : fallback.getAbsolutePath();
	}

	/** Writes the bundled renderer out to a temp folder, and returns that folder. */
	private static File extract() {
		File dir = new File(System.getProperty("java.io.tmpdir"), EXTRACT_DIR);
		if (!dir.isDirectory() && !dir.mkdirs()) {
			System.err.println("[DERanged] Could not create " + dir + " to unpack the software renderer into.");
			return null;
		}

		for (String fileName : DLLS) {
			File target = new File(dir, fileName);
			InputStream in = SoftwareGLFallback.class.getResourceAsStream("/" + DLL_RESOURCE_DIR + "/" + fileName);
			if (in == null) continue;

			try {
				if (target.isFile() && target.length() > 0) {
					continue;
				}
				//unpack to a temp file first, so a half written DLL is never loaded
				File unpacking = new File(dir, fileName + ".unpacking");
				Files.copy(in, unpacking.toPath(), StandardCopyOption.REPLACE_EXISTING);
				Files.move(unpacking.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				System.err.println("[DERanged] Could not unpack the software renderer (" + fileName + "): " + e);
				return null;
			} finally {
				try {
					in.close();
				} catch (IOException e) {
					//ignored
				}
			}
		}

		return dir;
	}
}
