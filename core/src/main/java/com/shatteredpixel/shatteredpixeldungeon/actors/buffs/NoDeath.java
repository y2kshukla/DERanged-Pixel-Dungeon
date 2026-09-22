package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Kromer;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

public class NoDeath extends FlavourBuff {

    {
        type = buffType.POSITIVE;

        announced = true;
    }

    public static final float DURATION	= 10f;

    @Override
    public int icon() {
        return BuffIndicator.RAGE;
    }

    @Override
    public float iconFadePercent() {
        float duration = 20;
        return Math.max(0, (duration - visualcooldown()) / duration);
    }

    @Override
    public boolean act() {
        if (target.HP <= 0) {
            target.HP = 0;
            target.die(Kromer.class);
            Dungeon.fail(Kromer.class);
            GLog.n( Messages.get(Kromer.class, "on_death") );
        }
        return super.act();
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
