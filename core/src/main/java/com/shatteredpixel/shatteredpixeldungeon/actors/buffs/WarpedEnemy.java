package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;

public class WarpedEnemy extends Buff {

    {
        type = buffType.NEGATIVE;
        announced = true;
    }

    @Override
    public int icon() {
        return BuffIndicator.WARP;
    }

    @Override
    public String toString() {
        return Messages.get(this, "name");
    }

    @Override
    public void fx(boolean on) {
        if (on) target.sprite.add(CharSprite.State.WARPED);
        else target.sprite.remove(CharSprite.State.WARPED);
    }

    {
        immunities.add(Charm.class);
        immunities.add(Vertigo.class);
        immunities.add(Terror.class);
    }

    //special variant, used for boss buffs
    public static class BossEffect extends Buff {
        @Override
        public void fx(boolean on) {
            if (on) target.sprite.add(CharSprite.State.WARPED);
            else target.sprite.remove(CharSprite.State.WARPED);
        }

        {
            immunities.add(Charm.class);
            immunities.add(Vertigo.class);
            immunities.add(Terror.class);
        }
    }
}