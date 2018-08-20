package azureForDemo.azureDemo;

import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.compute.AvailabilitySet;
import com.microsoft.azure.management.compute.AvailabilitySetSkuTypes;
import com.microsoft.azure.management.compute.VirtualMachineSizeTypes;
import com.microsoft.azure.management.network.Network;
import com.microsoft.azure.management.network.NetworkInterface;
import com.microsoft.azure.management.network.PublicIPAddress;
import com.microsoft.azure.management.resources.ResourceGroup;
import com.microsoft.azure.management.resources.fluentcore.arm.Region;
import com.microsoft.azure.management.resources.fluentcore.model.Indexable;
import com.microsoft.azure.management.resources.fluentcore.utils.SdkContext;
import com.microsoft.rest.LogLevel;
import rx.Observable;
import java.io.File;


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
            final String NIC_NAME = RG_NAME + "-nic1";
            final String VM_NAME = RG_NAME + "-vm1";
            Azure azure = getAzureClient();
            createResourceGroup(azure, RG_NAME);
            AvailabilitySet avSet = createAvailabilitySet(azure, RG_NAME, AV_NAME);
            PublicIPAddress ip = createPublicIPAddress(azure, RG_NAME, IP_NAME);
            Network network = createVirtualNetwork(azure, RG_NAME, SUBNET_NAME, VIRTUAL_NETWORK_NAME);
            NetworkInterface nic = createNIC(azure, RG_NAME, ip.id(), network.id(), SUBNET_NAME, NIC_NAME);
            createVM(azure, RG_NAME, nic.id(), avSet.id(), VM_NAME);
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
    public static AvailabilitySet createAvailabilitySet(Azure azure, String resourceGroup, String name) {
        System.out.println("Creating availability set...");
        AvailabilitySet availabilitySet = azure.availabilitySets()
            .define(name)
            .withRegion(Region.US_EAST)
            .withExistingResourceGroup(resourceGroup)
            .withSku(AvailabilitySetSkuTypes.MANAGED)
            .create();
        System.out.println("created availability set " + availabilitySet.id());
        return availabilitySet;
    }
    public static PublicIPAddress createPublicIPAddress(Azure azure, String resourceGroup, String name) {
        System.out.println("Creating public IP address...");
        PublicIPAddress publicIPAddress = azure.publicIPAddresses()
            .define(name)
            .withRegion(Region.US_EAST)
            .withExistingResourceGroup(resourceGroup)
            .withDynamicIP()
            .create();
        System.out.println("created ip address with id:" + publicIPAddress.id());
        System.out.println("created ip address: " + publicIPAddress.ipAddress());
        return publicIPAddress;
    }
    public static Network createVirtualNetwork(Azure azure, String resourceGroup, String subnetName, String name) {
        System.out.println("Creating virtual network...");
        Network network = azure.networks()
            .define(name)
            .withRegion(Region.US_EAST)
            .withExistingResourceGroup(resourceGroup)
            .withAddressSpace("10.0.0.0/16")
            .withSubnet(subnetName,"10.0.0.0/24")
            .create();
        System.out.println("created virtual network with id" + network.id());
        return network;
    }
    public static NetworkInterface createNIC(Azure azure, String resourceGroup, String ipId, String networkId, String subnetName, String name) {
        System.out.println("Creating network interface...");

        PublicIPAddress ip = azure.publicIPAddresses().getById(ipId);
        Network network = azure.networks().getById(networkId);
        NetworkInterface networkInterface = azure.networkInterfaces()
            .define(name)
            .withRegion(Region.US_EAST)
            .withExistingResourceGroup(resourceGroup)
            .withExistingPrimaryNetwork(network)
            .withSubnet(subnetName)
            .withPrimaryPrivateIPAddressDynamic()
            .withExistingPrimaryPublicIPAddress(ip)
            .create();
        System.out.println("created network interface with id: " + networkInterface.id());
        return networkInterface;
    }

    public static void createVM(Azure azure, String resourceGroup, String nicId, String avSetId, String name) {
        try {
            System.out.println("Start Creating a VM");
            NetworkInterface nic = azure.networkInterfaces().getById(nicId);
            AvailabilitySet avSet = azure.availabilitySets().getById(avSetId);
            Observable<Indexable> vmObservable = azure.virtualMachines()
                    .define(name)
                    .withRegion(Region.US_EAST)
                    .withExistingResourceGroup(resourceGroup)
                    .withExistingPrimaryNetworkInterface(nic)
                    .withLatestWindowsImage("MicrosoftWindowsServer", "WindowsServer", "2012-R2-Datacenter")
                    .withAdminUsername("azureuser")
                    .withAdminPassword("Azure12345678")
                    .withComputerName("myVM")
                    .withExistingAvailabilitySet(avSet)
                    .withSize(VirtualMachineSizeTypes.BASIC_A0)
                    .createAsync();
            System.out.println("created vm ");
            Thread.sleep(2000);
        }
        catch(Exception e) {
            System.out.println(e.toString());
        }
    }

    public static void DeleteResourceGroup(Azure azure, String resourceGroup) {
        azure.resourceGroups().deleteByName(resourceGroup);
    }
}