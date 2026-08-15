package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;

public class Scam extends FlavourBuff {

    {
        type = buffType.POSITIVE;

        announced = true;
    }

    public static final float DURATION	= 10f;

    @Override
    public int icon() {
        return BuffIndicator.SCAM;
    }

    @Override
    public float iconFadePercent() {
        float duration = 20;
        return Math.max(0, (duration - visualcooldown()) / duration);
    }

    @Override
    public String toString() {
        return Messages.get(this, "name");
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", dispTurns());
    }

}
