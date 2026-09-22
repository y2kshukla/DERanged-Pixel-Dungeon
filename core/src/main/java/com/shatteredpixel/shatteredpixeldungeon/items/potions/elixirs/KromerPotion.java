package com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Warp;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfStrength;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Kromer;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Random;

public class KromerPotion extends Elixir{

    {
        image = ItemSpriteSheet.EXOTIC_KRONER;
    }

    @Override
    public void apply(Hero hero) {
        Warp.inflict(50, 1f);
        hero.STR += 7;
        Buff.affect(hero, Effect.class);
        hero.sprite.showStatus( CharSprite.POSITIVE, Messages.get(this, "msg_1") );
        GLog.p( Messages.get(this, "msg_2") );
    }

    private static final ItemSprite.Glowing CHAOTIC = new ItemSprite.Glowing( 0.2f  );

    @Override
    public ItemSprite.Glowing glowing() {
        return CHAOTIC;
    }

    @Override
    public int value() {
        return quantity * Random.Int(6, 1341);
    }

    public static class Effect extends Buff {
        {
            revivePersists = true;
        }
    }

    public static class Recipe extends com.shatteredpixel.shatteredpixeldungeon.items.Recipe.SimpleRecipe {

        {
            inputs =  new Class[]{PotionOfStrength.class, Kromer.class};
            inQuantity = new int[]{1, 1};

            cost = 12;

            output = KromerPotion.class;
            outQuantity = 1;
        }

    }
}