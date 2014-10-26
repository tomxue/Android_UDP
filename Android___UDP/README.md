Workable example code of UDP sending and receiving on Android.

Local PC:
	Running SocketTool, to create a UDP Server: 0.0.0.0:5001
	ipconfig: shown as below, the IP should be 192.168.0.7
        $ ipconfig.exe

        Windows IP 配置

        无线局域网适配器 本地连接* 16:

           媒体状态  . . . . . . . . . . . . : 媒体已断开
           连接特定的 DNS 后缀 . . . . . . . :

        无线局域网适配器 WLAN 3:

           连接特定的 DNS 后缀 . . . . . . . :
           本地链接 IPv6 地址. . . . . . . . : fe80::5190:edf5:9f18:c9d6%21
           IPv4 地址 . . . . . . . . . . . . : 172.29.129.1
           子网掩码  . . . . . . . . . . . . : 255.255.0.0
           默认网关. . . . . . . . . . . . . :

        以太网适配器 以太网:

           连接特定的 DNS 后缀 . . . . . . . : router
           本地链接 IPv6 地址. . . . . . . . : fe80::89ed:956:9d33:1bfb%3
           IPv4 地址 . . . . . . . . . . . . : 192.168.0.7
           子网掩码  . . . . . . . . . . . . : 255.255.255.0
           默认网关. . . . . . . . . . . . . : 192.168.0.1

Android phone:
    Destination IP should be 192.168.0.7, and also could be 255.255.255.255 for Broadcast mode!
    Destination Port is 5001, which is set by SocketTool
    Local Port is set by Android, could be 5000
