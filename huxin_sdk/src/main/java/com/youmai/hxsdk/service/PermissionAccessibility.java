package com.youmai.hxsdk.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.List;

public class PermissionAccessibility extends AccessibilityService {
    private static final String TAG = PermissionAccessibility.class.getSimpleName();

    private boolean isPMFinish = false;
    private boolean isBGFinish = false;
    private boolean isFVFinish = false;

    String[] packageNames = new String[]{"com.android.settings", "com.google.android.packageinstaller",
            "com.samsung.android.packageinstaller", "com.meizu.safe", "com.coloros.safecenter",
            "com.android.packageinstaller", "com.huawei.systemmanager", "com.miui.securitycenter",
            "com.iqoo.secure", "com.youmai.huxin"};

    String[] permissions = new String[]{"位置", "存储", "电话", "相机", "短信", "通讯录", "麦克风"};

    String[] meizuPermissions = new String[]{"后台管理", "拨打电话", "锁屏下显示界面", "拨打电话", "发送短信",
            "读取联系人信息", "读取通话记录", "读取短信记录", "获取定位"};

    String[] oppoPermissions = new String[]{"电话", "读取通话记录", "读取联系人信息", "读取短信记录", "发送短信",
            "录音或通话录音", "读取位置信息"};

    String[] miPermissions = new String[]{"发送短信", "电话", "读取短信", "通知类短信", "监听电话",
            "读取联系人", "读取通话记录", "定位", "获取手机信息", "相机", "录音", "读写手机存储", "显示悬浮窗"};

    List<String> permissionsList = new ArrayList<>();

    @Override
    public void onServiceConnected() {
        Log.v(TAG, "PermissionAccessibility onServiceConnected");
        isPMFinish = false;
        isBGFinish = false;
        isFVFinish = false;

        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
        //info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        info.packageNames = packageNames;
        info.notificationTimeout = 100;
        setServiceInfo(info);

        String manufacturer = Build.MANUFACTURER.toLowerCase();
        switch (manufacturer) {
            case "huawei":
                break;
            case "xiaomi":
                for (String item : miPermissions) {
                    permissionsList.add(item);
                }
                break;
            case "oppo":
                for (String item : oppoPermissions) {
                    permissionsList.add(item);
                }
                break;
            case "vivo":
                break;
            case "meizu":
                for (String item : meizuPermissions) {
                    permissionsList.add(item);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();
        Log.v(TAG, "eventType:" + eventType);
        if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            String className = event.getClassName().toString();
            Log.v(TAG, "className:" + className);

            if (className.contains("com.youmai.hxsdk.activity.PermissionMainActivity")) {
                if (!isBGFinish) {
                    openBackgroudEnable();
                } else if (!isFVFinish) {
                    openFlowViewEnable();
                } else if (!isPMFinish) {
                    openPermission();
                }
            }


            String manufacturer = Build.MANUFACTURER.toLowerCase();
            switch (manufacturer) {
                case "huawei":
                    huawei(className);
                    break;
                case "xiaomi":
                    xiaomi(className);
                    break;
                case "oppo":
                    oppo(className);
                    break;
                case "vivo":
                    vivo(className);
                    break;
                case "meizu":
                    meizu(className);
                    break;
                case "lemobile":
                    lePhone(className);
                    break;
                case "samsung":
                    samsungC5(className);
                    break;
                default:
                    googleNexus(className);
                    break;
            }
        }
    }

    @Override
    public void onInterrupt() {
    }


    private void openPermission() {
        String manufacturer = Build.MANUFACTURER.toLowerCase();
        switch (manufacturer) {
            case "vivo":
                // vivo手机应用程序详情页面无应用权限设置入口
                // do nothing
                break;
            default:
                Uri packageURI = Uri.parse("package:" + getPackageName());
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
        }
    }

    private void openBackgroudEnable() {
        String manufacturer = Build.MANUFACTURER.toLowerCase();
        switch (manufacturer) {
            case "huawei":
                huaweiLockClear();
                break;
            case "xiaomi":
                xiaomiStartSelf();
                break;
            case "oppo":
                oppoStartSelf();
                break;
            case "vivo":
                vivoPermission();
                break;
            default:
                break;
        }
    }


    private void openFlowViewEnable() {
        String manufacturer = Build.MANUFACTURER.toLowerCase();
        switch (manufacturer) {
            case "huawei":
                break;
            case "xiaomi":
                // 悬浮窗权限在权限页面
                break;
            case "oppo":
                startOppoFloatWindow();
                break;
            case "vivo":
                break;
            default:
                break;
        }
    }


    private void startOppoFloatWindow() {
        Intent intent = new Intent();
        intent.setClassName("com.coloros.safecenter", "com.coloros.safecenter.sysfloatwindow.FloatWindowListActivity");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    private void oppoStartSelf() {
        Intent intent = new Intent();
        intent.setClassName("com.coloros.safecenter", "com.coloros.safecenter.startupapp.StartupAppListActivity");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    private void huaweiLockClear() {
        Intent intent = new Intent();
        intent.setClassName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void xiaomiStartSelf() {
        Intent intent = new Intent();
        //intent.setClassName("com.miui.securitycenter", "com.miui.securityscan.MainActivity");
        intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    private void vivoPermission() {
        Intent intent = new Intent();
        //intent.setClassName("com.iqoo.secure", "com.iqoo.secure.safeguard.PurviewTabActivity");
        //intent.setClassName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.SoftwareManagerActivity");
        intent.setClassName("com.iqoo.secure", "com.iqoo.secure.MainActivity");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    /**
     * android nexus5x
     *
     * @param className
     */
    @TargetApi(18)
    private void googleNexus(String className) {
        if (className.contains("com.android.settings.applications.InstalledAppDetailsTop")) {
            if (!isPMFinish) {
                AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
                if (nodeInfo != null) {
                    List<AccessibilityNodeInfo> list = nodeInfo
                            .findAccessibilityNodeInfosByText("权限");
                    if (list != null && list.size() > 0) {
                        AccessibilityNodeInfo parent = list.get(0).getParent();
                        parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                }
            } else {
                performGlobalAction(GLOBAL_ACTION_BACK);
            }
        } else if (className.contains("com.android.packageinstaller.permission.ui.ManagePermissionsActivity")) {
            AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
            if (nodeInfo != null) {
                for (String item : permissions) {
                    List<AccessibilityNodeInfo> list = nodeInfo
                            .findAccessibilityNodeInfosByText(item);
                    if (list != null && list.size() > 0) {
                        AccessibilityNodeInfo parent = list.get(0).getParent();
                        int count = parent.getChildCount();
                        AccessibilityNodeInfo checkBox = parent.getChild(count - 1);
                        if (checkBox != null && !checkBox.isChecked()) {
                            parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        }
                    }
                }
                performGlobalAction(GLOBAL_ACTION_BACK);
            }

            isPMFinish = true;
        }
    }


    /**
     * samsung c5
     *
     * @param className
     */
    @TargetApi(18)
    private void samsungC5(String className) {
        if (className.contains("com.android.settings.applications.InstalledAppDetailsTop")) {
            if (!isPMFinish) {
                AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
                if (nodeInfo != null) {
                    List<AccessibilityNodeInfo> list = nodeInfo
                            .findAccessibilityNodeInfosByText("权限");
                    if (list != null && list.size() > 0) {
                        AccessibilityNodeInfo parent = list.get(0).getParent();
                        parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                }
            } else {
                performGlobalAction(GLOBAL_ACTION_BACK);
            }
        } else if (className.contains("com.android.packageinstaller.permission.ui.ManagePermissionsActivity")) {
            AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
            if (nodeInfo != null) {
                for (String item : permissions) {
                    List<AccessibilityNodeInfo> list = nodeInfo
                            .findAccessibilityNodeInfosByText(item);
                    if (list != null && list.size() > 0) {
                        AccessibilityNodeInfo parent = list.get(0).getParent();
                        int count = parent.getChildCount();
                        AccessibilityNodeInfo checkBox = parent.getChild(count - 1);
                        if (checkBox != null && !checkBox.isChecked()) {
                            parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        }
                    }
                }
                performGlobalAction(GLOBAL_ACTION_BACK);
            }

            isPMFinish = true;
        }
    }


    /**
     * meizu
     *
     * @param className
     */
    @TargetApi(18)
    private void meizu(String className) {
        if (className.contains("com.android.settings.applications.InstalledAppDetailsTop")) {
            if (!isPMFinish) {
                AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
                if (nodeInfo != null) {
                    List<AccessibilityNodeInfo> list = nodeInfo
                            .findAccessibilityNodeInfosByText("权限管理");
                    if (list != null && list.size() > 0) {
                        AccessibilityNodeInfo parent = list.get(0).getParent();
                        parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                }
            } else {
                performGlobalAction(GLOBAL_ACTION_BACK);
            }
        } else if (className.contains("com.meizu.safe.security.AppSecActivity")) {
            AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
            if (nodeInfo != null) {
                if (permissionsList.size() > 0) {
                    String textItem = permissionsList.get(0);

                    List<AccessibilityNodeInfo> list = nodeInfo
                            .findAccessibilityNodeInfosByText(textItem);

                    if (list == null || list.size() == 0) {
                        List<AccessibilityNodeInfo> list1 = nodeInfo.findAccessibilityNodeInfosByViewId("android:id/list");
                        if (list1 != null && list1.size() > 0) {
                            AccessibilityNodeInfo node = list1.get(0);
                            if (node.isScrollable()) {
                                node.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                            }
                        }

                        list = nodeInfo.findAccessibilityNodeInfosByText(textItem);
                    }

                    if (list != null && list.size() > 0) {
                        AccessibilityNodeInfo parent = list.get(0).getParent();
                        if (parent != null) {
                            parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        }
                        permissionsList.remove(textItem);
                    }

                } else {
                    isPMFinish = true;
                    performGlobalAction(GLOBAL_ACTION_BACK);
                }
            }
        } else if (className.contains("android.app.AlertDialog")) {
            AccessibilityNodeInfo subInfo = getRootInActiveWindow();
            List<AccessibilityNodeInfo> subList = subInfo.findAccessibilityNodeInfosByText("保持后台运行");
            if (subList == null || subList.size() == 0) {
                subList = subInfo.findAccessibilityNodeInfosByText("允许");
            }
            if (subList != null && subList.size() > 0) {
                subList.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }
    }


    /**
     * oppo
     *
     * @param className
     */
    @TargetApi(18)
    private void oppo(String className) {
        if (className.contains("com.android.settings.applications.InstalledAppDetailsTop")) {
            if (!isPMFinish) {
                AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
                if (nodeInfo != null) {
                    List<AccessibilityNodeInfo> list = nodeInfo
                            .findAccessibilityNodeInfosByText("权限管理");
                    if (list != null && list.size() > 0) {
                        AccessibilityNodeInfo parent = list.get(0).getParent();
                        parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                } else {
                    performGlobalAction(GLOBAL_ACTION_BACK);
                }
            } else {
                performGlobalAction(GLOBAL_ACTION_BACK);
            }
        } else if (className.contains("com.coloros.safecenter.permission.PermissionAppAllPermissionActivity")) {
            AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
            if (nodeInfo != null) {
                if (permissionsList.size() > 0) {
                    String textItem = permissionsList.get(0);

                    List<AccessibilityNodeInfo> list = nodeInfo
                            .findAccessibilityNodeInfosByText(textItem);

                    while (list == null || list.size() == 0) {
                        List<AccessibilityNodeInfo> list1 = nodeInfo.findAccessibilityNodeInfosByViewId("android:id/list");
                        if (list1 != null && list1.size() > 0) {
                            AccessibilityNodeInfo node = list1.get(0);
                            if (node.isScrollable()) {
                                node.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                            }
                        }
                        list = nodeInfo.findAccessibilityNodeInfosByText(textItem);
                    }

                    if (list != null && list.size() > 0) {
                        AccessibilityNodeInfo parent = list.get(0).getParent();
                        if (parent != null) {
                            parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        }
                        permissionsList.remove(textItem);
                    }

                } else {
                    isPMFinish = true;
                    performGlobalAction(GLOBAL_ACTION_BACK);
                }
            }
        } else if (className.contains("com.coloros.safecenter.sysfloatwindow.FloatWindowListActivity")) {
            if (!isFVFinish) {
                AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
                if (nodeInfo != null) {
                    String textItem = "呼信";
                    List<AccessibilityNodeInfo> list = nodeInfo
                            .findAccessibilityNodeInfosByText(textItem);

                    while (list == null || list.size() == 0) {
                        List<AccessibilityNodeInfo> list1 = nodeInfo.findAccessibilityNodeInfosByViewId("android:id/list");
                        if (list1 != null && list1.size() > 0) {
                            AccessibilityNodeInfo node = list1.get(0);
                            if (node.isScrollable()) {
                                node.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                            }
                        }
                        list = nodeInfo.findAccessibilityNodeInfosByText(textItem);
                    }

                    if (list != null && list.size() > 0) {
                        AccessibilityNodeInfo parent = list.get(0).getParent();
                        if (parent != null) {
                            int count = parent.getChildCount();
                            AccessibilityNodeInfo checkBox = parent.getChild(count - 1);
                            if (checkBox != null && !checkBox.isChecked()) {
                                parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            }
                        }
                    }

                    isFVFinish = true;
                    performGlobalAction(GLOBAL_ACTION_BACK);

                }
            }
        } else if (className.contains("com.coloros.safecenter.startupapp.StartupAppListActivity")) {
            if (!isBGFinish) {
                AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
                if (nodeInfo != null) {
                    String textItem = "呼信";
                    List<AccessibilityNodeInfo> list = nodeInfo
                            .findAccessibilityNodeInfosByText(textItem);

                    while (list == null || list.size() == 0) {
                        List<AccessibilityNodeInfo> list1 = nodeInfo.findAccessibilityNodeInfosByViewId("android:id/list");
                        if (list1 != null && list1.size() > 0) {
                            AccessibilityNodeInfo node = list1.get(0);
                            if (node.isScrollable()) {
                                node.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                            }
                        }
                        list = nodeInfo.findAccessibilityNodeInfosByText(textItem);
                    }

                    if (list != null && list.size() > 0) {
                        AccessibilityNodeInfo parent = list.get(0).getParent();
                        if (parent != null) {
                            int count = parent.getChildCount();
                            AccessibilityNodeInfo checkBox = parent.getChild(count - 1);
                            if (checkBox != null && !checkBox.isChecked()) {
                                parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            }
                        }
                    }

                    isBGFinish = true;
                    performGlobalAction(GLOBAL_ACTION_BACK);

                }
            }
        } else if (className.contains("com.coloros.safecenter.common.view.CommonActivityDialog")) {
            AccessibilityNodeInfo subInfo = getRootInActiveWindow();
            List<AccessibilityNodeInfo> subList = subInfo.findAccessibilityNodeInfosByText("允许");

            if (subList != null && subList.size() > 0) {
                subList.get(0).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }

        }
    }


    /**
     * huawei
     *
     * @param className
     */
    @TargetApi(18)
    private void huawei(String className) {
        if (className.contains("com.android.settings.applications.InstalledAppDetailsTop")) {
            if (!isPMFinish) {
                AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
                if (nodeInfo != null) {
                    List<AccessibilityNodeInfo> list = nodeInfo
                            .findAccessibilityNodeInfosByText("权限");
                    if (list != null && list.size() > 0) {
                        AccessibilityNodeInfo parent = list.get(0).getParent();
                        parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                }
            } else {
                performGlobalAction(GLOBAL_ACTION_BACK);
            }
        } else if (className.contains("com.android.packageinstaller.permission.ui.ManagePermissionsActivity")) {
            if (!isPMFinish) {
                AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
                if (nodeInfo != null) {
                    for (String item : permissions) {
                        List<AccessibilityNodeInfo> list = nodeInfo
                                .findAccessibilityNodeInfosByText(item);
                        if (list != null && list.size() > 0) {
                            AccessibilityNodeInfo parent = list.get(0).getParent();
                            int count = parent.getChildCount();
                            AccessibilityNodeInfo checkBox = parent.getChild(count - 1);
                            if (checkBox != null && !checkBox.isChecked()) {
                                parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            }
                        }
                    }
                    List<AccessibilityNodeInfo> list1 = nodeInfo
                            .findAccessibilityNodeInfosByText("设置单项权限");
                    if (list1 != null && list1.size() > 0) {
                        AccessibilityNodeInfo parent = list1.get(0).getParent();
                        if (parent != null) {
                            parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        }
                    }

                }

            } else {
                performGlobalAction(GLOBAL_ACTION_BACK);
            }

        } else if (className.contains("com.huawei.permissionmanager.ui.SingleAppActivity")) {
            AccessibilityNodeInfo subInfo = getRootInActiveWindow();

            String[] subPermiss = new String[]{"信任此应用", "应用自动启动"};
            for (String item : subPermiss) {
                List<AccessibilityNodeInfo> list = subInfo
                        .findAccessibilityNodeInfosByText(item);
                if (list != null && list.size() > 0) {
                    AccessibilityNodeInfo parent = list.get(0).getParent();
                    int count = parent.getChildCount();
                    AccessibilityNodeInfo checkBox = parent.getChild(count - 1);
                    if (checkBox != null && !checkBox.isChecked()) {
                        parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                }
            }
            performGlobalAction(GLOBAL_ACTION_BACK);
            isPMFinish = true;
        } else if (className.contains("com.huawei.systemmanager.optimize.process.ProtectActivity")) {

            if (!isBGFinish) {
                AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();

                String textItem = "呼信";

                List<AccessibilityNodeInfo> list = nodeInfo
                        .findAccessibilityNodeInfosByText(textItem);

                while (list == null || list.size() == 0) {
                    List<AccessibilityNodeInfo> list1 = nodeInfo.
                            findAccessibilityNodeInfosByViewId("com.huawei.systemmanager:id/progress_manager_white_gridview");
                    if (list1 != null && list1.size() > 0) {
                        AccessibilityNodeInfo node = list1.get(0);
                        if (node.isScrollable()) {
                            node.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                        }
                    }
                    list = nodeInfo.findAccessibilityNodeInfosByText(textItem);
                }

                if (list != null && list.size() > 0) {
                    AccessibilityNodeInfo parent = list.get(0).getParent();

                    int count = parent.getChildCount();
                    AccessibilityNodeInfo checkBox = parent.getChild(count - 1);
                    if (checkBox != null && checkBox.isChecked()) {
                        parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }

                }

                isBGFinish = true;
                performGlobalAction(GLOBAL_ACTION_BACK);

            }
        }
    }


    /**
     * xiaomi
     *
     * @param className
     */
    @TargetApi(18)
    private void xiaomi(String className) {
        if (className.contains("com.android.settings.applications.InstalledAppDetailsTop")) {
            if (!isPMFinish) {
                AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
                String textItem = "权限管理";

                if (nodeInfo != null) {
                    List<AccessibilityNodeInfo> list = nodeInfo
                            .findAccessibilityNodeInfosByText(textItem);


                    while (list == null || list.size() == 0) {
                        List<AccessibilityNodeInfo> list1 = nodeInfo.findAccessibilityNodeInfosByViewId("android:id/list");
                        if (list1 != null && list1.size() > 0) {
                            AccessibilityNodeInfo node = list1.get(0);
                            if (node.isScrollable()) {
                                node.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                            }
                        }
                        list = nodeInfo.findAccessibilityNodeInfosByText(textItem);
                    }


                    if (list != null && list.size() > 0) {
                        AccessibilityNodeInfo parent = list.get(0).getParent();
                        parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                }
            } else {
                performGlobalAction(GLOBAL_ACTION_BACK);
            }
        } else if (className.contains("com.miui.permcenter.permissions.PermissionsEditorActivity")) {
            AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
            if (nodeInfo != null) {
                if (permissionsList.size() > 0) {
                    String textItem = permissionsList.get(0);

                    List<AccessibilityNodeInfo> list = nodeInfo
                            .findAccessibilityNodeInfosByText(textItem);

                    while (list == null || list.size() == 0) {
                        List<AccessibilityNodeInfo> list1 = nodeInfo.findAccessibilityNodeInfosByViewId("android:id/list");
                        if (list1 != null && list1.size() > 0) {
                            AccessibilityNodeInfo node = list1.get(0);
                            if (node.isScrollable()) {
                                node.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                            }
                        }

                        list = nodeInfo.findAccessibilityNodeInfosByText(textItem);
                    }

                    if (list != null && list.size() > 0) {
                        AccessibilityNodeInfo parent = list.get(0).getParent();
                        if (parent != null) {
                            parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        }
                        permissionsList.remove(textItem);
                    }

                } else {
                    isPMFinish = true;
                    performGlobalAction(GLOBAL_ACTION_BACK);
                }
            }
        } else if (className.contains("miui.app.AlertDialog")) {
            AccessibilityNodeInfo subInfo = getRootInActiveWindow();
            List<AccessibilityNodeInfo> subList = subInfo.findAccessibilityNodeInfosByText("允许");

            if (subList != null && subList.size() > 0) {
                subList.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }

        } else if (className.contains("com.miui.permcenter.autostart.AutoStartManagementActivity")) {
            AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
            if (nodeInfo != null) {
                String textItem = "呼信";
                List<AccessibilityNodeInfo> list = nodeInfo
                        .findAccessibilityNodeInfosByText(textItem);

                while (list == null || list.size() == 0) {
                    List<AccessibilityNodeInfo> list1 = nodeInfo.findAccessibilityNodeInfosByViewId("com.miui.securitycenter:id/list_view");
                    if (list1 != null && list1.size() > 0) {
                        AccessibilityNodeInfo node = list1.get(0);
                        if (node.isScrollable()) {
                            node.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                        }
                    }
                    list = nodeInfo.findAccessibilityNodeInfosByText(textItem);
                }

                if (list != null && list.size() > 0) {
                    AccessibilityNodeInfo parent = list.get(0).getParent();

                    List<AccessibilityNodeInfo> list2 = parent.findAccessibilityNodeInfosByViewId("com.miui.securitycenter:id/sliding_button");
                    if (list2 != null && list2.size() > 0) {
                        AccessibilityNodeInfo checkBox = list2.get(0);
                        if (checkBox != null && !checkBox.isChecked()) {
                            checkBox.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        }
                    }

                    /*int count = parent.getChildCount();
                    AccessibilityNodeInfo checkBox = parent.getChild(count - 1);
                    if (checkBox != null && !checkBox.isChecked()) {
                        //checkBox.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    } else {
                        isFinish = true;
                        performGlobalAction(GLOBAL_ACTION_BACK);
                    }*/
                }

                //isFinish = true;
                //performGlobalAction(GLOBAL_ACTION_BACK);
            }

        } else if (className.contains("com.miui.permcenter.autostart.AutoStartDetailManagementActivity")) {
            AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
            if (nodeInfo != null) {
                String[] textItems = new String[]{"允许系统唤醒", "允许被其他应用唤醒"};

                for (String textItem : textItems) {
                    List<AccessibilityNodeInfo> list = nodeInfo
                            .findAccessibilityNodeInfosByText(textItem);

                    if (list != null && list.size() > 0) {
                        AccessibilityNodeInfo parent = list.get(0).getParent();

                        int count = parent.getChildCount();
                        AccessibilityNodeInfo checkBox = parent.getChild(count - 1);
                        if (checkBox != null && !checkBox.isChecked()) {
                            checkBox.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        }

                    }
                }

                //isFinish = true;
                //performGlobalAction(GLOBAL_ACTION_BACK);
            }
        } else if (className.contains("com.miui.permcenter.MainAcitivty")) {
            if (!isPMFinish) {
                AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
                if (nodeInfo != null) {
                    List<AccessibilityNodeInfo> list = nodeInfo
                            .findAccessibilityNodeInfosByText("自启动管理");
                    if (list != null && list.size() > 0) {
                        AccessibilityNodeInfo parent = list.get(0).getParent();
                        parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                }
            } else {
                performGlobalAction(GLOBAL_ACTION_BACK);
            }
        } else if (className.contains("com.miui.securityscan.MainActivity")) {
            if (!isPMFinish) {
                AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
                if (nodeInfo != null) {
                    List<AccessibilityNodeInfo> list = nodeInfo
                            .findAccessibilityNodeInfosByText("授权管理");
                    if (list != null && list.size() > 0) {
                        AccessibilityNodeInfo parent = list.get(0);
                        parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                }
            } else {
                performGlobalAction(GLOBAL_ACTION_BACK);
            }
        }
    }


    /**
     * huawei
     *
     * @param className
     */
    @TargetApi(18)
    private void vivo(String className) {
        if (className.contains("com.iqoo.secure.MainActivity")) {
            if (!isBGFinish) {
                AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
                if (nodeInfo != null) {
                    List<AccessibilityNodeInfo> list = nodeInfo
                            .findAccessibilityNodeInfosByText("软件管理");
                    if (list != null && list.size() > 0) {
                        AccessibilityNodeInfo parent = list.get(0).getParent();
                        parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                }
            } else {
                performGlobalAction(GLOBAL_ACTION_BACK);
            }
        } else if (className.contains("com.iqoo.secure.ui.phoneoptimize.SoftwareManagerActivity")) {
            if (!isBGFinish) {
                AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
                if (nodeInfo != null) {
                    List<AccessibilityNodeInfo> list = nodeInfo
                            .findAccessibilityNodeInfosByText("软件权限管理");
                    if (list != null && list.size() > 0) {
                        AccessibilityNodeInfo parent = list.get(0).getParent();
                        parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                }
            } else {
                performGlobalAction(GLOBAL_ACTION_BACK);
            }
        } else if (className.contains("com.iqoo.secure.safeguard.PurviewTabActivity")) {
            if (!isBGFinish) {
                AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
                if (nodeInfo != null) {
                    List<AccessibilityNodeInfo> list = nodeInfo
                            .findAccessibilityNodeInfosByText("软件");
                    if (list != null && list.size() > 1) {
                        AccessibilityNodeInfo parent = list.get(1);
                        parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                }

                String textItem = "呼信";
                List<AccessibilityNodeInfo> list = nodeInfo
                        .findAccessibilityNodeInfosByText(textItem);
                while (list == null || list.size() == 0) {
                    List<AccessibilityNodeInfo> list1 = nodeInfo.
                            findAccessibilityNodeInfosByViewId("com.iqoo.secure:id/section_list_view");
                    if (list1 != null && list1.size() > 0) {
                        AccessibilityNodeInfo node = list1.get(0);
                        if (node.isScrollable()) {
                            node.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                        }
                    }
                    list = nodeInfo.findAccessibilityNodeInfosByText(textItem);
                }

                if (list != null && list.size() > 0) {
                    AccessibilityNodeInfo parent = list.get(0).getParent();
                    parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }

                isBGFinish = true;
            } else {
                performGlobalAction(GLOBAL_ACTION_BACK);
            }

        } else if (className.contains("com.iqoo.secure.safeguard.SoftPermissionDetailActivity")) {
            AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
            if (nodeInfo != null) {

                List<AccessibilityNodeInfo> list = nodeInfo
                        .findAccessibilityNodeInfosByViewId("com.iqoo.secure:id/lbsbutton");
                if (list != null && list.size() > 0) {
                    AccessibilityNodeInfo checkBox = list.get(0);
                    if (checkBox != null && !checkBox.isChecked()) {
                        checkBox.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                }

            }

            //isFinish = true;
            //performGlobalAction(GLOBAL_ACTION_BACK);
        }


    }


    /**
     * android 乐视
     *
     * @param className
     */
    @TargetApi(18)
    private void lePhone(String className) {
        if (className.contains("com.android.settings.applications.InstalledAppDetailsTop")) {
            if (!isPMFinish) {
                AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
                if (nodeInfo != null) {
                    List<AccessibilityNodeInfo> list = nodeInfo
                            .findAccessibilityNodeInfosByText("权限");
                    if (list != null && list.size() > 0) {
                        AccessibilityNodeInfo parent = list.get(0).getParent();
                        parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                }
            } else {
                performGlobalAction(GLOBAL_ACTION_BACK);
            }
        } else if (className.contains("com.android.packageinstaller.permission.ui.ManagePermissionsActivity")) {
            AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
            if (nodeInfo != null) {
                for (String item : permissions) {
                    List<AccessibilityNodeInfo> list = nodeInfo
                            .findAccessibilityNodeInfosByText(item);
                    if (list != null && list.size() > 0) {
                        AccessibilityNodeInfo parent = list.get(0).getParent();
                        int count = parent.getChildCount();
                        AccessibilityNodeInfo checkBox = parent.getChild(count - 1);
                        if (checkBox != null && !checkBox.isChecked()) {
                            parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        }
                    }
                }
                performGlobalAction(GLOBAL_ACTION_BACK);
            }

            isPMFinish = true;
        }
    }


}


