package com.shatteredpixel.shatteredpixeldungeon.items.quest;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Warp;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.Ratmogrify;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.adventurer.Root;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.adventurer.Sprout;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.archer.Hunt;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.archer.Snipe;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.cleric.PowerOfMany;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.duelist.ElementalStrike;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.gunner.FirstAidKit;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.gunner.Riot;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.huntress.NaturesPower;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.huntress.SpiritHawk;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.knight.HolyShield;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.medic.AngelWing;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.medic.HealingGenerator;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ratking.Wrath;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.rogue.ShadowClone;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.samurai.Awake;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.samurai.ShadowBlade;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.warrior.Endure;
import com.shatteredpixel.shatteredpixeldungeon.effects.Enchanting;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.RatKingArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.CursedWand;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.TalentsPane;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class Kromer extends Item {

    public static final String AC_USE	= "USE";
    public static final String AC_FOCUS	= "FOCUS";

    {
        image = ItemSpriteSheet.KROMER;
        stackable = true;
        cursed = true;
        cursedKnown = true;
        defaultAction = AC_FOCUS;
    }

    @Override
    public ArrayList<String> actions(Hero hero ) {
        ArrayList<String> actions = super.actions( hero );
        actions.add( AC_USE );
        actions.add( AC_FOCUS);
        return actions;
    }

    private static final ItemSprite.Glowing CHAOTIC = new ItemSprite.Glowing( 0.2f  );

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);

        if (action.equals(AC_USE)){
            GameScene.show(new WndOptions(Icons.get(Icons.TALENT), Messages.get(Kromer.class, "wnd_title"),
                    Messages.get(Kromer.class, "wnd_message"),
                    Messages.get(TalentsPane.class, "tier", 1), Messages.get(TalentsPane.class, "tier", 2), Messages.get(TalentsPane.class, "tier", 3)
            ){
                @Override
                protected boolean enabled(int index) {
                    return Dungeon.hero.lvl >= Talent.tierLevelThresholds[index+1];
                }

                @Override
                protected void onSelect(int index) {
                    if (Dungeon.hero.talents.get(index).size() > (index == 2 ? 13 : 7)) {
                        GLog.n(Messages.get(Kromer.class, "too_many"));
                        return;
                    }
                    HeroClass cls;
                    Talent randomTalent = null;
                    while (randomTalent == null) {
                        do {
                            cls = Random.element(HeroClass.values());
                        } while (cls == Dungeon.hero.heroClass);
                        randomTalent = Random.element(Talent.talentList(cls, index + 1));
                    }
                    Dungeon.hero.talents.get(index).put(randomTalent, 0);
                    Sample.INSTANCE.play( Assets.Sounds.LEVELUP );
                    Dungeon.hero.sprite.emitter().burst(Speck.factory(Speck.STAR), 40);
                    Enchanting.show(Dungeon.hero, Kromer.this);
                    GLog.p(Messages.get(Kromer.class, "new_talent"));
                    detach(Dungeon.hero.belongings.backpack);
                    Warp.inflict(50, 1.5f);
                }
            });
        } else if (action.equals(AC_FOCUS)){
            GameScene.selectCell(focus);
        }
    }

    private CellSelector.Listener focus = new CellSelector.Listener() {

        private int t = -1;

        @Override
        public void onSelect(Integer cell) {
            if (cell != null) {
                GLog.n(Messages.get(Kromer.class, "no_way_back"));
                Dungeon.hero.sprite.zap(cell, () -> {
                    t = cell;
                    final Ballistica shot = new Ballistica(curUser.pos, cell, Ballistica.PROJECTILE);
                    CursedWand.cursedZap(Kromer.this, Dungeon.hero, shot, this::shoot);
                });
            }
        }

        public void shoot() {
            Ballistica shot = new Ballistica(Dungeon.hero.pos, t, Ballistica.PROJECTILE);
            Sample.INSTANCE.play(Assets.Sounds.CHARGEUP);
            Warp.inflict(10, 0.3f);
            Dungeon.hero.HP = Math.min(Dungeon.hero.HT, Dungeon.hero.HP + 2);
            curUser.busy();
            curUser.spendAndNext(1f);
            if (curUser.HP >= curUser.HT*0.5f && Random.Int(4) == 0){
                Dungeon.hero.sprite.zap(t, () -> {
                    CursedWand.cursedZap(Kromer.this, Dungeon.hero, shot, this::shoot);
                });
            } else {
                Dungeon.hero.ready();
            }
        }

        @Override
        public String prompt() {
            return Messages.get(Wand.class, "prompt");
        }
    };

    @Override
    public ItemSprite.Glowing glowing() {
        return CHAOTIC;
    }

    @Override
    protected void onThrow(int cell) {
        RatKingArmor armor = new RatKingArmor();
        armor.charge = 100f;
        Splash.at(cell, 0x0bd74e, 60);
        Actor.addDelayed(new Pushing(Dungeon.hero, Dungeon.hero.pos, Dungeon.hero.pos, () -> {
            GameScene.flash(0xfdfa31);
            Splash.at(cell, 0xfdfa31, 60);
            for (ArmorAbility ability : new ArmorAbility[]{
                    new Ratmogrify(), new ShadowClone(), new SpiritHawk(), new PowerOfMany(),
                    new Endure(), new Awake(), new ShadowBlade(), new Root(), new Sprout(),
                    new Hunt(), new Snipe(), new ElementalStrike(), new Riot(), new FirstAidKit(),
                    new NaturesPower(), new HolyShield(), new HealingGenerator(), new AngelWing(),
                    new Wrath()})
                ability.activate(armor, Dungeon.hero, cell);
            Warp.inflict(120, 0.33f);
        }), -1);
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public int value() {
        return quantity * Random.Int(1, 672);
    }
}