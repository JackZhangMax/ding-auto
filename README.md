1.windwos安装adb -- 转载自https://blog.csdn.net/x2584179909/article/details/108319973
  下载adb
  Windows版本：https://dl.google.com/android/repository/platform-tools-latest-windows.zip
  按键windows+r打开运行，输入sysdm.cpl，回车。
  高级》环境变量》系统变量》path
  添加adb路径至环境变量
  测试adb安装结果 abd --version
  
2.安卓设备将钉钉放入主界面 并抓取坐标(打开开发者模式>打开指针位置功能)
3.完善配置
4.下载钉钉内网穿透（用于外网调用接口手动打卡或关闭钉钉打卡）
  下载及配置地址：https://developers.dingtalk.com/document/resourcedownload/http-intranet-penetration
5.配置百度ocr（用于识别打卡结果）
6.配置通知软件
  ios建议使用bark
  安卓用户建议使用微信配置方法请参考https://sct.ftqq.com/sendkey
7.设置端口启动
  建议：可使用软件设置为windows服务方便自启动（参考https://blog.csdn.net/tanhongwei1994/article/details/90044612） 
  BIOS设置为通电自动开机（自行百度）
