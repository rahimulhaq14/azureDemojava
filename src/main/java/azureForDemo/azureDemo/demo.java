package azureForDemo.azureDemo;

import com.microsoft.azure.AzureEnvironment;
import com.microsoft.azure.PagedList;
import com.microsoft.azure.credentials.UserTokenCredentials;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.compute.AvailabilitySet;
import com.microsoft.azure.management.compute.AvailabilitySetSkuTypes;
import com.microsoft.azure.management.compute.ComputeResourceType;
import com.microsoft.azure.management.compute.ComputeSku;
import com.microsoft.azure.management.compute.DiskInstanceView;
import com.microsoft.azure.management.compute.InstanceViewStatus;
import com.microsoft.azure.management.compute.KnownLinuxVirtualMachineImage;
import com.microsoft.azure.management.compute.VirtualMachine;
import com.microsoft.azure.management.compute.VirtualMachineSizeTypes;
import com.microsoft.azure.management.cosmosdb.CosmosDBAccount;
import com.microsoft.azure.management.cosmosdb.DatabaseAccountKind;
import com.microsoft.azure.management.network.Network;
import com.microsoft.azure.management.network.NetworkInterface;
import com.microsoft.azure.management.network.PublicIPAddress;
import com.microsoft.azure.management.resources.ResourceGroup;
import com.microsoft.azure.management.resources.fluentcore.arm.AvailabilityZoneId;
import com.microsoft.azure.management.resources.fluentcore.arm.Region;
import com.microsoft.rest.LogLevel;

import java.io.File;
import java.util.Map;
import java.util.Set;


public final class demo {
	static String password = "suemeiuselinux??";
    /**
     * Main function which runs the actual sample.
     * @param azure instance of the azure client
     * @return true if sample runs successfully
     */
    public static boolean runSample(Azure azure) {
    	
        //=================================================================
        // List all compute SKUs in the subscription
        //
        System.out.println("Listing Compute SKU in the subscription");
        String format = "%-22s %-16s %-22s %s";

        System.out.println(String.format(format, "Name", "ResourceType", "Size", "Regions [zones]"));
        System.out.println("============================================================================");

        PagedList<ComputeSku> skus = azure.computeSkus().list();
        for (ComputeSku sku : skus) {
            String size = null;
            if (sku.resourceType().equals(ComputeResourceType.VIRTUALMACHINES)) {
                size = sku.virtualMachineSizeType().toString();
            } else if (sku.resourceType().equals(ComputeResourceType.AVAILABILITYSETS)) {
                size = sku.availabilitySetSkuType().toString();
            } else if (sku.resourceType().equals(ComputeResourceType.DISKS)) {
                size = sku.diskSkuType().toString();
            } else if (sku.resourceType().equals(ComputeResourceType.SNAPSHOTS)) {
                size = sku.diskSkuType().toString();
            }
            Map<Region, Set<AvailabilityZoneId>> regionZones = sku.zones();
            System.out.println(String.format(format, sku.name(), sku.resourceType(), size, regionZoneToString(sku.zones())));
        }

        //=================================================================
        // List compute SKUs for a specific compute resource type (VirtualMachines) in a region
        //
        System.out.println("Listing compute SKUs for a specific compute resource type (VirtualMachines) in a region (US East2)");
        format = "%-22s %-22s %s";

        System.out.println(String.format(format, "Name", "Size", "Regions [zones]"));
        System.out.println("============================================================================");

        skus = azure.computeSkus()
                .listbyRegionAndResourceType(Region.US_EAST2, ComputeResourceType.VIRTUALMACHINES);
        for (ComputeSku sku : skus) {
            final String line = String.format(format, sku.name(), sku.virtualMachineSizeType(), regionZoneToString(sku.zones()));
            System.out.println(line);
        }

        //=================================================================
        // List compute SKUs for a specific compute resource type (Disks)
        //
        System.out.println("Listing compute SKUs for a specific compute resource type (Disks)");
        format = "%-22s %-22s %s";

        System.out.println(String.format(format, "Name", "Size", "Regions [zones]"));
        System.out.println("============================================================================");

        skus = azure.computeSkus()
                .listByResourceType(ComputeResourceType.DISKS);
        for (ComputeSku sku : skus) {
            final String line = String.format(format, sku.name(), sku.diskSkuType(), regionZoneToString(sku.zones()));
            System.out.println(line);
        }

        return true;
    }

    private static String regionZoneToString(Map<Region, Set<AvailabilityZoneId>> regionZonesMap) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<Region, Set<AvailabilityZoneId>> regionZones : regionZonesMap.entrySet()) {
            builder.append(regionZones.getKey().toString());
            builder.append(" [ ");
            for (AvailabilityZoneId zone :regionZones.getValue()) {
                builder.append(zone).append(" ");
            }
            builder.append("] ");
        }
        return builder.toString();
    }

    /**
     * The main entry point.
     *
     * @param args the parameters
     */
    public static void main(String[] args) {
        try {

            final File credFile = new File("/Users/rahimulhaq/eclipse-workspace/azureDemo/src/main/java/azureForDemo/azureDemo/auth");
        	
            azure = Azure.configure()
                    .withLogLevel(LogLevel.NONE)
                    .authenticate(credFile)
                    .withDefaultSubscription();
            System.out.println(azure);
//            runSample(azure);
            long start_time = System.nanoTime();
            createVM();
//            createCosmoDB();
            long end_time = System.nanoTime();
        	long total_time = end_time - start_time;
        	long in_secs = total_time/(1000 * 1000 * 1000);
        	System.out.println(in_secs/60);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
    static Azure azure;
public static void createVM() {
	System.out.println("Creating a Linux VM");
	
	System.out.println("Creating resource group...");
	ResourceGroup resourceGroup = azure.resourceGroups()
	    .define("myResourceGroup1")
	    .withRegion(Region.US_EAST)
	    .create();
	
	System.out.println("Creating availability set...");
	AvailabilitySet availabilitySet = azure.availabilitySets()
	    .define("myAvailabilitySet")
	    .withRegion(Region.US_EAST)
	    .withExistingResourceGroup("myResourceGroup1")
	    .withSku(AvailabilitySetSkuTypes.MANAGED)
	    .create();
	
	System.out.println("Creating public IP address...");
	PublicIPAddress publicIPAddress = azure.publicIPAddresses()
	    .define("myPublicIP")
	    .withRegion(Region.US_EAST)
	    .withExistingResourceGroup("myResourceGroup1")
	    .withDynamicIP()
	    .create();
	
	
	System.out.println("Creating virtual network...");
	Network network = azure.networks()
	    .define("myVN")
	    .withRegion(Region.US_EAST)
	    .withExistingResourceGroup("myResourceGroup1")
	    .withAddressSpace("10.0.0.0/16")
	    .withSubnet("mySubnet","10.0.0.0/24")
	    .create();
	
	
	System.out.println("Creating network interface...");
	NetworkInterface networkInterface = azure.networkInterfaces()
	    .define("myNIC1")
	    .withRegion(Region.US_EAST)
	    .withExistingResourceGroup("myResourceGroup1")
	    .withExistingPrimaryNetwork(network)
	    .withSubnet("mySubnet")
	    .withPrimaryPrivateIPAddressDynamic()
	    .withExistingPrimaryPublicIPAddress(publicIPAddress)
	    .create();
	
	System.out.println("Creating VM....");
	
	VirtualMachine vm = azure.virtualMachines()
		    .define("myVM1")
		    .withRegion(Region.US_EAST)
		    .withExistingResourceGroup("myResourceGroup1")
		    .withExistingPrimaryNetworkInterface(networkInterface)
		    .withLatestWindowsImage("MicrosoftWindowsServer", "WindowsServer", "2012-R2-Datacenter")
		    .withAdminUsername("azureuser")
		    .withAdminPassword("Azure12345678")
		    .withComputerName("myVM")
		    .withExistingAvailabilitySet(availabilitySet)
		    .withSize(VirtualMachineSizeTypes.STANDARD_D3_V2)
		    .create();
	
	/*Can uncomment when needed*/
	
//	System.out.println("hardwareProfile");
//	System.out.println("    vmSize: " + vm.size());
//	System.out.println("storageProfile");
//	System.out.println("  imageReference");
//	System.out.println("    publisher: " + vm.storageProfile().imageReference().publisher());
//	System.out.println("    offer: " + vm.storageProfile().imageReference().offer());
//	System.out.println("    sku: " + vm.storageProfile().imageReference().sku());
//	System.out.println("    version: " + vm.storageProfile().imageReference().version());
//	System.out.println("  osDisk");
//	System.out.println("    osType: " + vm.storageProfile().osDisk().osType());
//	System.out.println("    name: " + vm.storageProfile().osDisk().name());
//	System.out.println("    createOption: " + vm.storageProfile().osDisk().createOption());
//	System.out.println("    caching: " + vm.storageProfile().osDisk().caching());
//	System.out.println("osProfile");
//	System.out.println("    computerName: " + vm.osProfile().computerName());
//	System.out.println("    adminUserName: " + vm.osProfile().adminUsername());
//	System.out.println("    provisionVMAgent: " + vm.osProfile().windowsConfiguration().provisionVMAgent());
//	System.out.println("    enableAutomaticUpdates: " + vm.osProfile().windowsConfiguration().enableAutomaticUpdates());
//	System.out.println("networkProfile");
//	System.out.println("    networkInterface: " + vm.primaryNetworkInterfaceId());
//	System.out.println("vmAgent");
//	System.out.println("  vmAgentVersion: " + vm.instanceView().vmAgent().vmAgentVersion());
//	System.out.println("    statuses");
//	for(InstanceViewStatus status : vm.instanceView().vmAgent().statuses()) {
//	    System.out.println("    code: " + status.code());
//	    System.out.println("    displayStatus: " + status.displayStatus());
//	    System.out.println("    message: " + status.message());
//	    System.out.println("    time: " + status.time());
//	}
//	System.out.println("disks");
//	for(DiskInstanceView disk : vm.instanceView().disks()) {
//	    System.out.println("  name: " + disk.name());
//	    System.out.println("  statuses");
//	    for(InstanceViewStatus status : disk.statuses()) {
//	        System.out.println("    code: " + status.code());
//	        System.out.println("    displayStatus: " + status.displayStatus());
//	        System.out.println("    time: " + status.time());
//	    }
//	}
//	System.out.println("VM general status");
//	System.out.println("  provisioningStatus: " + vm.provisioningState());
//	System.out.println("  id: " + vm.id());
//	System.out.println("  name: " + vm.name());
//	System.out.println("  type: " + vm.type());
//	System.out.println("VM instance status");
	for(InstanceViewStatus status : vm.instanceView().statuses()) {
//	    System.out.println("  code: " + status.code());
	    System.out.println("  displayStatus: " + status.displayStatus());
	}
}

public static void createCosmoDB() {
	
	long start_time = System.nanoTime();
	
	
	CosmosDBAccount cosmosDBAccount = azure.cosmosDBAccounts().define("cosmoDBRahim1")
			.withRegion(Region.US_EAST)
			.withNewResourceGroup("cosmoResource")
			.withKind(DatabaseAccountKind.GLOBAL_DOCUMENT_DB)
			.withSessionConsistency()
			.withWriteReplication(Region.US_WEST)
			.withReadReplication(Region.US_CENTRAL)
			.create();
	long end_time = System.nanoTime();
	long total_time = end_time - start_time;
	long in_secs = total_time/(1000 * 1000 * 1000);
	System.out.println(in_secs);
}
}