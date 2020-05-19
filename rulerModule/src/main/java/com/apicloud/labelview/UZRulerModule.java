package com.apicloud.labelview;

import android.content.Intent;

import com.uzmap.pkg.uzcore.UZWebView;
import com.uzmap.pkg.uzcore.uzmodule.UZModule;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;

import org.json.JSONObject;

import static android.app.Activity.RESULT_OK;

public class UZRulerModule extends UZModule {

    public static JSONObject labelJSONObject;
    private static final int REQUEST_CODE = 7812;
    private UZModuleContext moduleContext;

    public UZRulerModule(UZWebView webView) {
        super(webView);
    }

    public void jsmethod_showImageEditVC(final UZModuleContext moduleContext) {
        this.moduleContext = moduleContext;
        labelJSONObject = moduleContext.getJSONContext().asJSONObject();
        Intent intent = new Intent(activity(), LabelActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            moduleContext.success(labelJSONObject, true);
        }
    }
}
