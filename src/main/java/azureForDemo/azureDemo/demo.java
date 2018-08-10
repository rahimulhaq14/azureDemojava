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
import com.microsoft.azure.management.resources.fluentcore.model.Indexable;
import com.microsoft.azure.management.resources.fluentcore.utils.SdkContext;
import com.microsoft.rest.LogLevel;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.io.File;
import java.util.Map;
import java.util.Set;


public final class demo {

    /**
     * The main entry point.
     *
     * @param args the parameters
     */
    public static void main(String[] args) {
        try {            
            String prefix = createRandomName("ed-azure-java");
            final String RG_NAME = prefix + "-rg1";
            final String AV_NAME = RG_NAME + "-av1";
            final String IP_NAME = RG_NAME + "-ip1";
            final String SUBNET_NAME = RG_NAME + "-subnet1";
            final String VIRTUAL_NETWORK_NAME = RG_NAME + "-vn1";            
            Azure azure = getAzureClient();
            createResourceGroup(azure, RG_NAME);
            createAvailabilitySet(azure, RG_NAME, AV_NAME);
            createPublicIPAddress(azure, RG_NAME, IP_NAME);
            createVirtualNetwork(azure, RG_NAME, SUBNET_NAME, VIRTUAL_NETWORK_NAME);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
    public static String createRandomName(String namePrefix) {
        return SdkContext.randomResourceName(namePrefix, 30);
    }

    public static Azure getAzureClient() throws Exception {
        final File credFile = new File(System.getProperty("user.dir") + "/src/main/java/azureForDemo/azureDemo/auth");
        return Azure.configure()
                .withLogLevel(LogLevel.NONE)
                .authenticate(credFile)
                .withDefaultSubscription();
    }
    public static void createResourceGroup(Azure azure, String name) {
        System.out.println("Creating resource group...");
        ResourceGroup resourceGroup = azure.resourceGroups()
            .define(name)
            .withRegion(Region.US_EAST)
            .create();
        System.out.println("created resource group" + resourceGroup.name());
    } 
    public static void createAvailabilitySet(Azure azure, String resourceGroup, String name) {
        System.out.println("Creating availability set...");
        AvailabilitySet availabilitySet = azure.availabilitySets()
            .define(name)
            .withRegion(Region.US_EAST)
            .withExistingResourceGroup(resourceGroup)
            .withSku(AvailabilitySetSkuTypes.MANAGED)
            .create();
        System.out.println("created availability set " + availabilitySet.name());
    }
    public static void createPublicIPAddress(Azure azure, String resourceGroup, String name) {
        System.out.println("Creating public IP address...");
        PublicIPAddress publicIPAddress = azure.publicIPAddresses()
            .define(name)
            .withRegion(Region.US_EAST)
            .withExistingResourceGroup(resourceGroup)
            .withDynamicIP()
            .create();
        System.out.println("created ip address " + publicIPAddress.name());
    }
    public static void createVirtualNetwork(Azure azure, String resourceGroup, String subnetName, String name) {
        System.out.println("Creating virtual network...");
        Network network = azure.networks()
            .define(name)
            .withRegion(Region.US_EAST)
            .withExistingResourceGroup(resourceGroup)
            .withAddressSpace("10.0.0.0/16")
            .withSubnet(subnetName,"10.0.0.0/24")
            .create();
        System.out.println("created virtual network" + network.name());
    }
    public static void createVM() {
        try {
            System.out.println("Start Creating a VM");
        
            
            
            
            
            
            
            
            
            
            
            // System.out.println("Creating network interface...");
            // NetworkInterface networkInterface = azure.networkInterfaces()
            //     .define("myNIC1")
            //     .withRegion(Region.US_EAST)
            //     .withExistingResourceGroup("myResourceGroup1")
            //     .withExistingPrimaryNetwork(network)
            //     .withSubnet("mySubnet")
            //     .withPrimaryPrivateIPAddressDynamic()
            //     .withExistingPrimaryPublicIPAddress(publicIPAddress)
            //     .create();
            // System.out.println("Creating VM....");
            
            
            // Observable<Indexable> vmObservable = azure.virtualMachines()
            //         .define("myVM1")
            //         .withRegion(Region.US_EAST)
            //         .withExistingResourceGroup("myResourceGroup1")
            //         .withExistingPrimaryNetworkInterface(networkInterface)
            //         .withLatestWindowsImage("MicrosoftWindowsServer", "WindowsServer", "2012-R2-Datacenter")
            //         .withAdminUsername("azureuser")
            //         .withAdminPassword("Azure12345678")
            //         .withComputerName("myVM")
            //         .withExistingAvailabilitySet(availabilitySet)
            //         .withSize(VirtualMachineSizeTypes.STANDARD_D3_V2)
            //         .createAsync();
            
            // vmObservable.subscribe();
            /*Can uncomment when needed*/
            

            // System.out.println("created vm async");
            // Thread.sleep(1000);
        }
        catch(Exception e) {
            System.out.println("exception caught");
        }
    }
}