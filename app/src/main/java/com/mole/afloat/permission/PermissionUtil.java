package com.mole.afloat.permission;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.util.LogUtil;

import com.mole.afloat.permission.rom.HuaweiUtils;
import com.mole.afloat.permission.rom.MeizuUtils;
import com.mole.afloat.permission.rom.MiuiUtils;
import com.mole.afloat.permission.rom.OppoUtils;
import com.mole.afloat.permission.rom.QikuUtils;
import com.mole.afloat.permission.rom.RomUtils;
import com.mole.afloat.utils.DialogUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class PermissionUtil {
    public static boolean checkPermission(Context context) {
        //6.0 版本之后由于 google 增加了对悬浮窗权限的管理，所以方式就统一了
        if (Build.VERSION.SDK_INT < 23) {
            if (RomUtils.checkIsMiuiRom()) {
                return miuiPermissionCheck(context);
            } else if (RomUtils.checkIsMeizuRom()) {
                return meizuPermissionCheck(context);
            } else if (RomUtils.checkIsHuaweiRom()) {
                return huaweiPermissionCheck(context);
            } else if (RomUtils.checkIs360Rom()) {
                return qikuPermissionCheck(context);
            } else if (RomUtils.checkIsOppoRom()) {
                return oppoROMPermissionCheck(context);
            }
        }
        return commonROMPermissionCheck(context);
    }

    public static void applyPermission(Context context) {
        if (Build.VERSION.SDK_INT < 23) {
            if (RomUtils.checkIsMiuiRom()) {
                miuiROMPermissionApply(context);
            } else if (RomUtils.checkIsMeizuRom()) {
                meizuROMPermissionApply(context);
            } else if (RomUtils.checkIsHuaweiRom()) {
                huaweiROMPermissionApply(context);
            } else if (RomUtils.checkIs360Rom()) {
                ROM360PermissionApply(context);
            } else if (RomUtils.checkIsOppoRom()) {
                oppoROMPermissionApply(context);
            }
        } else {
            commonROMPermissionApply(context);
        }
    }

    private static void ROM360PermissionApply(final Context context) {
        showConfirmDialog(context, new PermissionUtil.OnConfirmResult() {
            @Override
            public void confirmResult(boolean confirm) {
                if (confirm) {
                    QikuUtils.applyPermission(context);
                } else {
                    LogUtil.e("ROM:360, user manually refuse OVERLAY_PERMISSION");
                }
            }
        });
    }

    private static void huaweiROMPermissionApply(final Context context) {
        showConfirmDialog(context, new PermissionUtil.OnConfirmResult() {
            @Override
            public void confirmResult(boolean confirm) {
                if (confirm) {
                    HuaweiUtils.applyPermission(context);
                } else {
                    LogUtil.e("ROM:huawei, user manually refuse OVERLAY_PERMISSION");
                }
            }
        });
    }

    private static void meizuROMPermissionApply(final Context context) {
        showConfirmDialog(context, new PermissionUtil.OnConfirmResult() {
            @Override
            public void confirmResult(boolean confirm) {
                if (confirm) {
                    MeizuUtils.applyPermission(context);
                } else {
                    LogUtil.e("ROM:meizu, user manually refuse OVERLAY_PERMISSION");
                }
            }
        });
    }

    private static void miuiROMPermissionApply(final Context context) {
        showConfirmDialog(context, new PermissionUtil.OnConfirmResult() {
            @Override
            public void confirmResult(boolean confirm) {
                if (confirm) {
                    MiuiUtils.applyMiuiPermission(context);
                } else {
                    LogUtil.e("ROM:miui, user manually refuse OVERLAY_PERMISSION");
                }
            }
        });
    }

    private static void oppoROMPermissionApply(final Context context) {
        showConfirmDialog(context, new PermissionUtil.OnConfirmResult() {
            @Override
            public void confirmResult(boolean confirm) {
                if (confirm) {
                    OppoUtils.applyOppoPermission(context);
                } else {
                    LogUtil.e("ROM:miui, user manually refuse OVERLAY_PERMISSION");
                }
            }
        });
    }

    public static void commonROMPermissionApplyInternal(Context context) throws NoSuchFieldException, IllegalAccessException {
        Class clazz = Settings.class;
        Field field = clazz.getDeclaredField("ACTION_MANAGE_OVERLAY_PERMISSION");

        Intent intent = new Intent(field.get(null).toString());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        context.startActivity(intent);
    }

    /**
     * 通用 rom 权限申请
     */
    private static void commonROMPermissionApply(final Context context) {
        //这里也一样，魅族系统需要单独适配
        if (RomUtils.checkIsMeizuRom()) {
            meizuROMPermissionApply(context);
        } else {
            if (Build.VERSION.SDK_INT >= 23) {
                showConfirmDialog(context, new PermissionUtil.OnConfirmResult() {
                    @Override
                    public void confirmResult(boolean confirm) {
                        if (confirm) {
                            try {
                                commonROMPermissionApplyInternal(context);
                            } catch (Exception e) {
                                LogUtil.e(Log.getStackTraceString(e));
                            }
                        } else {
                            LogUtil.d("user manually refuse OVERLAY_PERMISSION");
                            //需要做统计效果
                        }
                    }
                });
            }
        }
    }

    private static void showConfirmDialog(Context context, OnConfirmResult result) {
        showConfirmDialog(context, "您的手机没有授予悬浮窗权限，请开启后再试", result);
    }

    private static void showConfirmDialog(Context context, String message, final OnConfirmResult result) {
        DialogUtil.showAlertDialog(context, message, "确认", "取消", true, new DialogUtil.AlertDialogBtnClickListener() {
            @Override
            public void clickPositive() {
                result.confirmResult(true);
            }

            @Override
            public void clickNegative() {
                result.confirmResult(false);
            }
        });
    }

    private static boolean commonROMPermissionCheck(Context context) {
        //最新发现魅族6.0的系统这种方式不好用，天杀的，只有你是奇葩，没办法，单独适配一下
        if (RomUtils.checkIsMeizuRom()) {
            return meizuPermissionCheck(context);
        } else {
            Boolean result = true;
            if (Build.VERSION.SDK_INT >= 23) {
                try {
                    Class clazz = Settings.class;
                    Method canDrawOverlays = clazz.getDeclaredMethod("canDrawOverlays", Context.class);
                    result = (Boolean) canDrawOverlays.invoke(null, context);
                } catch (Exception e) {
                    LogUtil.e(Log.getStackTraceString(e));
                }
            }
            return result;
        }
    }

    private static boolean huaweiPermissionCheck(Context context) {
        return HuaweiUtils.checkFloatWindowPermission(context);
    }

    private static boolean miuiPermissionCheck(Context context) {
        return MiuiUtils.checkFloatWindowPermission(context);
    }

    private static boolean meizuPermissionCheck(Context context) {
        return MeizuUtils.checkFloatWindowPermission(context);
    }

    private static boolean qikuPermissionCheck(Context context) {
        return QikuUtils.checkFloatWindowPermission(context);
    }

    private static boolean oppoROMPermissionCheck(Context context) {
        return OppoUtils.checkFloatWindowPermission(context);
    }

    private interface OnConfirmResult {
        void confirmResult(boolean confirm);
    }

}
