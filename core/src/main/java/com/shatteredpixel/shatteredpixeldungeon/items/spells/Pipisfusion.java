package com.shatteredpixel.shatteredpixeldungeon.items.spells;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.KromerParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Kromer;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRemoveCurse;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class Pipisfusion extends InventorySpell {

    {
        image = ItemSpriteSheet.PIPISFUSION;
    }

    @Override
    protected boolean usableOnItem(Item item) {
        return (item instanceof MeleeWeapon);
    }

    @Override
    protected void onItemSelected(Item item) {

        CellEmitter.get(curUser.pos).burst(KromerParticle.FACTORY, 15);
        Sample.INSTANCE.play(Assets.Sounds.CURSED, 2f, 2f);

        ScrollOfRemoveCurse.uncurse(Dungeon.hero, item);
        if (item instanceof MeleeWeapon) {
            MeleeWeapon w = (MeleeWeapon) item;
            w.enchant();
            w.trollers = true;
            if (w instanceof MagesStaff){
                ((MagesStaff) w).updateWand(true);
            }
        }
        Badges.validateItemLevelAquired(item);
        updateQuickslot();
    }

    @Override
    public int value() {
        //prices of ingredients, divided by output quantity
        return Math.round(quantity * ((27 + Random.Int(1, 672)) / 2f));
    }

    public static class Recipe extends com.shatteredpixel.shatteredpixeldungeon.items.Recipe.SimpleRecipe {

        {
            inputs =  new Class[]{CurseInfusion.class, Kromer.class};
            inQuantity = new int[]{1, 1};

            cost = 22;

            output = Pipisfusion.class;
            outQuantity = 2;
        }

    }
}
