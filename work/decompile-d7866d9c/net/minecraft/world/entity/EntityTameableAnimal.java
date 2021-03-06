package net.minecraft.world.entity;

import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.advancements.CriterionTriggers;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.players.NameReferencingFileConverter;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.animal.EntityAnimal;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.World;
import net.minecraft.world.scores.ScoreboardTeamBase;

public abstract class EntityTameableAnimal extends EntityAnimal {

    protected static final DataWatcherObject<Byte> bo = DataWatcher.a(EntityTameableAnimal.class, DataWatcherRegistry.a);
    protected static final DataWatcherObject<Optional<UUID>> bp = DataWatcher.a(EntityTameableAnimal.class, DataWatcherRegistry.o);
    private boolean willSit;

    protected EntityTameableAnimal(EntityTypes<? extends EntityTameableAnimal> entitytypes, World world) {
        super(entitytypes, world);
        this.eL();
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.datawatcher.register(EntityTameableAnimal.bo, (byte) 0);
        this.datawatcher.register(EntityTameableAnimal.bp, Optional.empty());
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        if (this.getOwnerUUID() != null) {
            nbttagcompound.a("Owner", this.getOwnerUUID());
        }

        nbttagcompound.setBoolean("Sitting", this.willSit);
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        UUID uuid;

        if (nbttagcompound.b("Owner")) {
            uuid = nbttagcompound.a("Owner");
        } else {
            String s = nbttagcompound.getString("Owner");

            uuid = NameReferencingFileConverter.a(this.getMinecraftServer(), s);
        }

        if (uuid != null) {
            try {
                this.setOwnerUUID(uuid);
                this.setTamed(true);
            } catch (Throwable throwable) {
                this.setTamed(false);
            }
        }

        this.willSit = nbttagcompound.getBoolean("Sitting");
        this.setSitting(this.willSit);
    }

    @Override
    public boolean a(EntityHuman entityhuman) {
        return !this.isLeashed();
    }

    public boolean isTamed() {
        return ((Byte) this.datawatcher.get(EntityTameableAnimal.bo) & 4) != 0;
    }

    public void setTamed(boolean flag) {
        byte b0 = (Byte) this.datawatcher.get(EntityTameableAnimal.bo);

        if (flag) {
            this.datawatcher.set(EntityTameableAnimal.bo, (byte) (b0 | 4));
        } else {
            this.datawatcher.set(EntityTameableAnimal.bo, (byte) (b0 & -5));
        }

        this.eL();
    }

    protected void eL() {}

    public boolean isSitting() {
        return ((Byte) this.datawatcher.get(EntityTameableAnimal.bo) & 1) != 0;
    }

    public void setSitting(boolean flag) {
        byte b0 = (Byte) this.datawatcher.get(EntityTameableAnimal.bo);

        if (flag) {
            this.datawatcher.set(EntityTameableAnimal.bo, (byte) (b0 | 1));
        } else {
            this.datawatcher.set(EntityTameableAnimal.bo, (byte) (b0 & -2));
        }

    }

    @Nullable
    public UUID getOwnerUUID() {
        return (UUID) ((Optional) this.datawatcher.get(EntityTameableAnimal.bp)).orElse((Object) null);
    }

    public void setOwnerUUID(@Nullable UUID uuid) {
        this.datawatcher.set(EntityTameableAnimal.bp, Optional.ofNullable(uuid));
    }

    public void tame(EntityHuman entityhuman) {
        this.setTamed(true);
        this.setOwnerUUID(entityhuman.getUniqueID());
        if (entityhuman instanceof EntityPlayer) {
            CriterionTriggers.x.a((EntityPlayer) entityhuman, (EntityAnimal) this);
        }

    }

    @Nullable
    public EntityLiving getOwner() {
        try {
            UUID uuid = this.getOwnerUUID();

            return uuid == null ? null : this.world.b(uuid);
        } catch (IllegalArgumentException illegalargumentexception) {
            return null;
        }
    }

    @Override
    public boolean c(EntityLiving entityliving) {
        return this.i(entityliving) ? false : super.c(entityliving);
    }

    public boolean i(EntityLiving entityliving) {
        return entityliving == this.getOwner();
    }

    public boolean a(EntityLiving entityliving, EntityLiving entityliving1) {
        return true;
    }

    @Override
    public ScoreboardTeamBase getScoreboardTeam() {
        if (this.isTamed()) {
            EntityLiving entityliving = this.getOwner();

            if (entityliving != null) {
                return entityliving.getScoreboardTeam();
            }
        }

        return super.getScoreboardTeam();
    }

    @Override
    public boolean r(Entity entity) {
        if (this.isTamed()) {
            EntityLiving entityliving = this.getOwner();

            if (entity == entityliving) {
                return true;
            }

            if (entityliving != null) {
                return entityliving.r(entity);
            }
        }

        return super.r(entity);
    }

    @Override
    public void die(DamageSource damagesource) {
        if (!this.world.isClientSide && this.world.getGameRules().getBoolean(GameRules.SHOW_DEATH_MESSAGES) && this.getOwner() instanceof EntityPlayer) {
            this.getOwner().sendMessage(this.getCombatTracker().getDeathMessage(), SystemUtils.b);
        }

        super.die(damagesource);
    }

    public boolean isWillSit() {
        return this.willSit;
    }

    public void setWillSit(boolean flag) {
        this.willSit = flag;
    }
}
