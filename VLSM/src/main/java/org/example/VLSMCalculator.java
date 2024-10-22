package org.example;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class VLSMCalculator {

    // Chuyển đổi từ địa chỉ IP thành số nguyên
    public static int ipToInt(String ipAddress) {
        String[] octets = ipAddress.split("\\.");
        return (Integer.parseInt(octets[0]) << 24) |
                (Integer.parseInt(octets[1]) << 16) |
                (Integer.parseInt(octets[2]) << 8) |
                Integer.parseInt(octets[3]);
    }

    // Chuyển đổi từ số nguyên thành địa chỉ IP
    public static String intToIp(int ip) {
        return String.format("%d.%d.%d.%d",
                (ip >> 24) & 0xFF,
                (ip >> 16) & 0xFF,
                (ip >> 8) & 0xFF,
                ip & 0xFF);
    }

    // Tính toán địa chỉ broadcast dựa trên địa chỉ mạng và subnet mask
    public static String getBroadcastAddress(int networkAddress, int subnetMask) {
        int broadcast = networkAddress | (~subnetMask);
        return intToIp(broadcast);
    }

    // Chuyển subnet mask dạng số bit thành dạng thập phân
    public static String convertToSubnetMask(int subnetBits) {
        int mask = 0xffffffff << (32 - subnetBits);  // Tạo subnet mask dưới dạng số nguyên
        return String.format("%d.%d.%d.%d",
                (mask >> 24) & 0xff,
                (mask >> 16) & 0xff,
                (mask >> 8) & 0xff,
                mask & 0xff);
    }

    // Chia mạng cho LAN
    public static String calculateSubnet(String networkAddress, int hostCount, String lanName, int initialSubnetBits) {
        // Tính toán số lượng bit cần thiết cho host
        int m = (int) Math.ceil(Math.log(hostCount + 2) / Math.log(2)); // 2 bit thêm vào để loại trừ địa chỉ mạng và broadcast
        int subnetBits = 32 - m;  // Số bit dành cho subnet


        // Giữ giá trị n1 cố định bằng initialSubnetBits
        int n1 = initialSubnetBits;

        // Đảm bảo subnetBits không nhỏ hơn initialSubnetBits
        if (subnetBits < n1) {
            subnetBits = n1;
        }

//        if (subnetBits < initialSubnetBits) {
//            subnetBits = initialSubnetBits;
//        }
//

        // Chuyển IP thành số nguyên
        int baseIP = ipToInt(networkAddress);

        // Tính subnet mask
        String subnetMask = convertToSubnetMask(subnetBits);
        int subnetMaskInt = ipToInt(subnetMask);

        // Tính địa chỉ broadcast
        String broadcastAddress = getBroadcastAddress(baseIP, subnetMaskInt);

        // Tính bước nhảy
        int stepSize = 1 << (32 - subnetBits);  // Bước nhảy = 2^(32 - subnetBits)
        System.out.println(lanName + " Số lượng host đã nhập: " + hostCount);
        System.out.println(lanName + " LỖI n: = (32 - " + n1 + " - " + m + ") = " + (32 - n1 - m));
        System.out.println(lanName + " m: = (2^m-2 >= " + hostCount + ") => m: " + m);

        System.out.println(lanName + " Subnet Bits: = " + "32 - m = " + "32 - " + m + " = " + subnetBits + " =>/" + subnetBits);
        System.out.println(lanName + " Step: " + "2^m = " + "2^" + m + " = " + stepSize);  // In bước nhảy
        System.out.println(lanName + " LỖI Số lượng mạng con được chia: 2^n= 2^" + (32 - n1 - m) + " = " + Math.pow(2, (32 - n1 - m)));

        System.out.println(lanName + " Network Address: " + networkAddress + "/" + subnetBits);
        System.out.println(lanName + " Subnet Mask: " + subnetMask);
        System.out.println(lanName + " Broadcast Address: " + broadcastAddress);
        System.out.println("---");

        // Trả về địa chỉ IP tiếp theo cho mạng kế tiếp
        int nextNetwork = baseIP + stepSize;  // Tăng giá trị của mạng con để lấy mạng tiếp theo
        return intToIp(nextNetwork);
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in, StandardCharsets.UTF_8); // Ensure input is in UTF-8

        // Input the main network address and CIDR
        System.out.println("Nhập địa chỉ IP mạng kèm CIDR (ví dụ: 192.168.23.0/24):");
        String ipWithCidr = sc.nextLine();

        // Split the IP and CIDR
        String[] ipParts = ipWithCidr.split("/");
        String networkAddress = ipParts[0];
        int initialSubnetBits = Integer.parseInt(ipParts[1]); // Get the CIDR value

        // Input the number of LANs and corresponding hosts
        System.out.println("Nhập số lượng mạng con (LAN):");
        int lanCount = sc.nextInt();
        int[] hosts = new int[lanCount];
        int[] lanIndices = new int[lanCount];  // Array to store original LAN indices
        sc.nextLine();  // clear the buffer

        for (int i = 0; i < lanCount; i++) {
            System.out.println("Nhập số lượng host cho LAN" + (i + 1) + ":");
            hosts[i] = sc.nextInt();
            lanIndices[i] = i + 1;  // Store the original LAN index
        }

        // Sort the LANs by host count (descending) while keeping the original indices
        for (int i = 0; i < lanCount - 1; i++) {
            for (int j = i + 1; j < lanCount; j++) {
                if (hosts[i] < hosts[j]) {
                    int tempHost = hosts[i];
                    hosts[i] = hosts[j];
                    hosts[j] = tempHost;

                    // Swap corresponding LAN indices
                    int tempIndex = lanIndices[i];
                    lanIndices[i] = lanIndices[j];
                    lanIndices[j] = tempIndex;
                }
            }
        }

        // Subdivide the network for each LAN based on the entered host counts
        for (int i = 0; i < lanCount; i++) {
            String lanName = "LAN" + lanIndices[i];  // Retrieve the LAN name based on the original order
            networkAddress = calculateSubnet(networkAddress, hosts[i], lanName, initialSubnetBits);
        }

        sc.close(); // Close the Scanner
    }
}