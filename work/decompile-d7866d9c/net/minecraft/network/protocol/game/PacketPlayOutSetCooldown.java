package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.item.Item;

public class PacketPlayOutSetCooldown implements Packet<PacketListenerPlayOut> {

    private Item a;
    private int b;

    public PacketPlayOutSetCooldown() {}

    public PacketPlayOutSetCooldown(Item item, int i) {
        this.a = item;
        this.b = i;
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = Item.getById(packetdataserializer.i());
        this.b = packetdataserializer.i();
    }

    @Override
    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.d(Item.getId(this.a));
        packetdataserializer.d(this.b);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }
}
