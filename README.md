# BlueToothDemo

A BlueTooth chat demo

一个蓝牙聊天示范

主要代码在ManiActivity中。仅作蓝牙通信示例，内存管理做的还不够细致，如实际使用，请自行处理。

2021/3/17
完善了BLE通讯。但一般ble主要应用场景是app与其他穿戴等物联网设备通讯。
无需编写服务端代码。app仅作客户端发送数据。
自动连接的地方有点问题。服务端可以连上客户端，客户端由于UUID是自拟的，获取服务的时候会有问题。
如果一直连不上，就需要手动到蓝牙设置里取消两个设备的配对。然后客户端重新连接就好了。
本示例仅供参考。

Copyright Dullyoung 
For learning reference only, please indicate the source when reproduced.

Dullyoung 版权所有，仅供学习参考，转载请标明出处

