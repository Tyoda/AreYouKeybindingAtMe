package org.tyoda.wurm.client.aykam;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import org.gotti.wurmunlimited.modloader.classhooks.HookException;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;
import org.gotti.wurmunlimited.modloader.interfaces.PreInitable;
import org.gotti.wurmunlimited.modloader.interfaces.Versioned;
import org.gotti.wurmunlimited.modloader.interfaces.WurmClientMod;

import java.util.logging.Logger;

public class AreYouKeybindingAtMe implements WurmClientMod, PreInitable, Versioned {
    private static final Logger logger = Logger.getLogger(AreYouKeybindingAtMe.class.getName());
    public static final String version = "1.0";

    @Override
    public void preInit() {
        try {
            ClassPool classPool = HookManager.getInstance().getClassPool();
            CtClass ctWurmSettingsFX = classPool.getCtClass("com.wurmonline.client.launcherfx.WurmSettingsFX");

            logger.info("Instrumenting addKeybind");
            ctWurmSettingsFX.getDeclaredMethod("addKeybind").instrument(new ExprEditor(){
                @Override
                public void edit(MethodCall m) throws CannotCompileException {
                    if("add".equals(m.getMethodName())) {
                        m.replace("if(!$0.contains($1)) { $_ = $proceed($$); }");
                        logger.info("Editing add at line "+m.getLineNumber());
                    }
                }
            });

            logger.info("Finished.");
        } catch (NotFoundException | CannotCompileException e) {
            throw new HookException(e);
        }
    }

    @Override
    public String getVersion() {
        return version;
    }
}