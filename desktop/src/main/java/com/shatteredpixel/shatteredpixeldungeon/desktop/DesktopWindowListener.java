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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Window;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowListener;
import com.badlogic.gdx.graphics.GL20;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.watabou.noosa.audio.Music;

public class DesktopWindowListener implements Lwjgl3WindowListener {
	
	@Override
	public void created ( Lwjgl3Window lwjgl3Window ) {
		//Reported because it is useful to know which OpenGL driver the game ended up
		//using: it tells users why the game might be slow, and it lets the automated
		//Windows builds check that the game really got as far as a window.
		System.out.println("[DERanged] Window created using the " + SoftwareGLFallback.currentMode() + ".");
		System.out.flush();

		//OpenGL cannot be queried yet at this point, so the driver details are
		//reported once the game is actually rendering
		if (Gdx.app != null) {
			Gdx.app.postRunnable(new Runnable() {
				@Override
				public void run () {
					String renderer;
					try {
						renderer = Gdx.gl.glGetString( GL20.GL_RENDERER )
								+ " (OpenGL " + Gdx.gl.glGetString( GL20.GL_VERSION ) + ")";
					} catch (Throwable t) {
						renderer = "unknown";
					}
					System.out.println("[DERanged] OpenGL renderer: " + renderer);
					System.out.flush();
				}
			});
		}
	}
	
	@Override
	public void maximized ( boolean b ) {
		SPDSettings.windowMaximized( b );
		if (b){
			SPDSettings.windowResolution(DesktopPlatformSupport.previousSizes[1]);
		}
	}
	
	@Override
	public void iconified ( boolean b ) { }
	public void focusLost () {
		if (!SPDSettings.playMusicInBackground()) {
			Music.INSTANCE.pause();
		}
	}
	public void focusGained () {
		if (!SPDSettings.playMusicInBackground()){
			Music.INSTANCE.resume();
		}
	}
	public boolean closeRequested () { return true; }
	public void filesDropped ( String[] strings ) { }
	public void refreshRequested () { }
}
