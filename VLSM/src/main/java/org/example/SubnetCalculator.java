package org.example;

import java.util.Scanner;

public class SubnetCalculator {

    // Chuyển đổi một số nguyên thành địa chỉ IP
    public static String intToIp(int ip) {
        return ((ip >> 24) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                (ip & 0xFF);
    }

    // Chuyển đổi một địa chỉ IP thành số nguyên
    public static int ipToInt(String ip) {
        String[] octets = ip.split("\\.");
        return (Integer.parseInt(octets[0]) << 24) |
                (Integer.parseInt(octets[1]) << 16) |
                (Integer.parseInt(octets[2]) << 8) |
                Integer.parseInt(octets[3]);
    }

    public static String calculateSubnetMask(int prefixLength) {
        int mask = 0xffffffff << (32 - prefixLength); // Tạo subnet mask dưới dạng số nguyên
        return String.format("%d.%d.%d.%d",
                (mask >> 24) & 0xff,
                (mask >> 16) & 0xff,
                (mask >> 8) & 0xff,
                mask & 0xff);
    }

    // Tính toán subnet
    public static void calculateSubnet(String networkAddress, int prefixLength, int requiredHosts, String lanName) {
        // Tính số bit cần cho phần host (m)
        int bitsForHosts = (int) Math.ceil(Math.log(requiredHosts + 2) / Math.log(2));  // Số bit cần cho phần host
        int newPrefixLength = 32 - bitsForHosts; // Cập nhật prefixLength
        int n = (32 - prefixLength - bitsForHosts); // Số lượng bit cần mượn thêm ở phần host_id để chia mạng

        int totalHosts = (int) Math.pow(2, bitsForHosts);  // Số lượng IP trong subnet
        int step = totalHosts;  // Bước nhảy giữa các mạng con

        int networkInt = ipToInt(networkAddress);  // Chuyển đổi IP ban đầu thành số nguyên

        int stepSize = 1 << (32 - newPrefixLength);  // Bước nhảy = 2^(32 - subnetBits)

        System.out.println(lanName + ": " + requiredHosts);
        System.out.println("  - m (number of bits for host_id): " + "m = (2^m-2 >= " + requiredHosts + ") => m = " + bitsForHosts);
        System.out.println("  - n (number of bits borrowed): " + "n" + " = " + "32 - " + prefixLength + " + " + bitsForHosts + " = " + n);
        System.out.println("  - Subnet Bits: = " + "32 - m = " + "32 - " + bitsForHosts + " = " + newPrefixLength + " =>/" + newPrefixLength);
        System.out.println("  - Step (Hosts per subnet): " + "2^m = " + "2^" + bitsForHosts + " = " + stepSize);
        System.out.println("  - Number of subnets divided: 2^n= 2^" + n + " = " + Math.pow(2, n));
        System.out.println("");
        System.out.println("  - Network Address: " + networkAddress + "/" + newPrefixLength);
        System.out.println("  - Subnet Mask: " + calculateSubnetMask(newPrefixLength));
        System.out.println("  - Broadcast Address: " + intToIp(networkInt + step - 1));
//        System.out.println("  - Usable IPs: " + intToIp(networkInt + 1) + " - " + intToIp(networkInt + step - 2));
        System.out.println("---");
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Nhập địa chỉ mạng và CIDR
        System.out.print("Nhập địa chỉ mạng kèm CIDR (ví dụ: 192.168.23.0/24): ");
        String ipWithCidr = scanner.nextLine();

        // Tách địa chỉ mạng và prefixLength
        String[] parts = ipWithCidr.split("/");
        String networkAddress = parts[0];
        int prefixLength = Integer.parseInt(parts[1]);

        // Tính toán subnet cho các LAN
        calculateSubnet(networkAddress, prefixLength, 50, "LAN2");  // 50 hosts cho LAN2

        // LAN1 được lấy từ subnet tiếp theo
        calculateSubnet("192.168.23.64", 26, 30, "LAN1");  // 30 hosts cho LAN1

        // LAN3 được lấy từ subnet tiếp theo sau LAN1
        calculateSubnet("192.168.23.96", 27, 10, "LAN3");  // 10 hosts cho LAN3

        scanner.close(); // Đóng Scanner
    }
}
