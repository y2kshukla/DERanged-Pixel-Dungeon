package com.shatteredpixel.shatteredpixeldungeon.effects.particles;

import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.Random;

import java.util.Arrays;

public class KromerParticle extends PixelParticle {

    public static final Emitter.Factory FACTORY = new Emitter.Factory() {
        @Override
        public void emit( Emitter emitter, int index, float x, float y ) {
            emitter.recycle( KromerParticle.class ).reset( x, y );
        }
        @Override
        public boolean lightMode() {
            return true;
        }
    };

    public KromerParticle() {
        super();

        lifespan = 1f;
        color( Random.element(Arrays.asList(
                0x00ff54, 0xf6e316, 0xff51c2, 0x4f4573)) );

        acc.set( 0, +30 );
    }

    public void reset( float x, float y ) {
        revive();

        left = lifespan;

        size = 10;
        this.x = x;
        this.y = y;

        speed.polar( -Random.Float( 3.1415926f ), Random.Float( 6 ) );
    }

    @Override
    public void update() {
        super.update();

        float p = left / lifespan;
        am = p < 0.5f ? p * p * 4 : (1 - p) * 2;
        size( Random.Float( 6 * (left / lifespan) ) );
    }
}
