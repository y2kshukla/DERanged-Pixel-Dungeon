package com.shatteredpixel.shatteredpixeldungeon.items.spells;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Scam;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Warp;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Kromer;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class ScammingSpell extends Spell{

    {
        image = ItemSpriteSheet.ALCHEMIZE;
    }

    @Override
    protected void onCast(Hero hero) {
        Warp.inflict(50, 1.5f);
        Buff.prolong(hero, Scam.class, 35f);
        hero.sprite.operate(hero.pos);
        Sample.INSTANCE.play(Assets.Sounds.CURSED, 1f, 0.3f);
        hero.sprite.emitter().burst( Speck.factory( Speck.STENCH ), 40);
        GLog.p(Messages.get(this, "apply"));

        detach( curUser.belongings.backpack );
        updateQuickslot();
        hero.spendAndNext( 1f );
    }

    @Override
    public int value() {
        return quantity * Random.Int(5, 890);
    }

    private static final ItemSprite.Glowing CHAOTIC = new ItemSprite.Glowing( 0.2f  );

    @Override
    public ItemSprite.Glowing glowing() {
        return CHAOTIC;
    }

    public static class Recipe extends com.shatteredpixel.shatteredpixeldungeon.items.Recipe.SimpleRecipe {

        {
            inputs =  new Class[]{Kromer.class, UnstableSpell.class};
            inQuantity = new int[]{1, 1};

            cost = 22;

            output = ScammingSpell.class;
            outQuantity = 11;
        }

    }
}