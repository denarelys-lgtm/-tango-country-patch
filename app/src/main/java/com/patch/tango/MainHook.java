package com.patch.tango;

import android.content.Context;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class MainHook implements IXposedHookLoadPackage {

    // Codigo ISO de 2 letras del pais a forzar (ej. US, MX, ES, AR)
    private static final String TARGET_COUNTRY = "US";

    @Override
    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("com.sgiggle.production")) return;

        // 1. Force Web Login for Google Sign-In (Bypass Signature Check)
        try {
            XposedHelpers.findAndHookMethod(
                "com.google.android.gms.common.GoogleApiAvailability",
                lpparam.classLoader,
                "isGooglePlayServicesAvailable",
                Context.class,
                new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        return 1; // 1 = SERVICE_MISSING
                    }
                }
            );
        } catch (Throwable ignored) {}

        // 2. Hooking en RemoteCountryCodeSource
        try {
            XposedHelpers.findAndHookMethod(
                "me.tango.utils.countrypicker.data.RemoteCountryCodeSource",
                lpparam.classLoader,
                "lookupCountryByISOCode",
                String.class,
                new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        return TARGET_COUNTRY;
                    }
                }
            );
        } catch (Throwable ignored) {}

        // 3. Hooking en DefaultSimCountryCodeProvider (xX0/z)
        try {
            XposedHelpers.findAndHookMethod(
                "xX0.z",
                lpparam.classLoader,
                "getSimCountryIso",
                new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        return TARGET_COUNTRY.toLowerCase();
                    }
                }
            );
        } catch (Throwable ignored) {}

        // 4. Hooking en DefaultNetworkCountryCodeProvider (xX0/u)
        try {
            XposedHelpers.findAndHookMethod(
                "xX0.u",
                lpparam.classLoader,
                "getNetworkCountryIso",
                new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        return TARGET_COUNTRY.toLowerCase();
                    }
                }
            );
        } catch (Throwable ignored) {}
    }
}
