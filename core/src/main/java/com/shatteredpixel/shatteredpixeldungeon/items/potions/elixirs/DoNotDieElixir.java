package com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.NoDeath;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Warp;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.UnstableBrew;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Kromer;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class DoNotDieElixir extends Elixir{

    {
        image = ItemSpriteSheet.POTION_JADE;
    }

    @Override
    public void apply(Hero hero) {
        Warp.inflict(50, 1f);
        Buff.prolong(hero, NoDeath.class, 250f);
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
            inputs =  new Class[]{Kromer.class, UnstableBrew.class};
            inQuantity = new int[]{1, 1};

            cost = 12;

            output = DoNotDieElixir.class;
            outQuantity = 1;
        }

    }
}
