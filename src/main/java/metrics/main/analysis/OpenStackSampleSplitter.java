package metrics.main.analysis;

/**
 * Created by anton on 4/28/16.
 */
public class OpenStackSampleSplitter extends PhysicalHostSampleSplitter {

    public OpenStackSampleSplitter fillInfo() {
        // TODO query info from OpenStack
//        splitter.ovsdb_ids.put("patch-int", "...");
        libvirt_ids.put("instance-0000061d", "ralf.ims");
        libvirt_ids.put("instance-0000060b", "ns.ims");
        libvirt_ids.put("instance-0000060e", "ellis.ims");
        libvirt_ids.put("instance-00000611", "hs.ims");
        libvirt_ids.put("instance-00000617", "bono.ims");
        libvirt_ids.put("instance-00000614", "sprout.ims");
        libvirt_ids.put("instance-0000061a", "homer.ims");
        return this;
    }

}
