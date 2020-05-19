package com.apicloud.labelview;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonUtil {
    public static LabelBean getLabelBean(JSONObject jsonObject) {
        LabelBean labelBean = new LabelBean();
        try {
            String title = jsonObject.optString("title");
            String picString = jsonObject.optString("picString");
            JSONArray jsonArray = jsonObject.optJSONArray("labelList");
            List<LabelBean.Bean> list = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = (JSONObject) jsonArray.get(i);
                String startX = obj.optString("startX");
                String startY = obj.optString("startY");
                String endX = obj.optString("endX");
                String endY = obj.optString("endY");
                String labelText = obj.optString("labelText");
                LabelBean.Bean bean = new LabelBean.Bean(string2Float(startX), string2Float(startY), string2Float(endX), string2Float(endY), labelText);
                list.add(bean);
            }
            labelBean.title = title;
            labelBean.picString = picString;
            labelBean.labelList = list;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return labelBean;
    }

    public static JSONObject getJSONObject(LabelBean labelBean) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("picString", labelBean.picString);
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < labelBean.labelList.size(); i++) {
                LabelBean.Bean bean = labelBean.labelList.get(i);
                JSONObject obj = new JSONObject();
                obj.put("startX", bean.startX);
                obj.put("startY", bean.startY);
                obj.put("endX", bean.endX);
                obj.put("endY", bean.endY);
                obj.put("labelText", bean.labelText);
                jsonArray.put(obj);
            }
            jsonObject.put("labelList", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private static float string2Float(String s) {
        try {
            return Float.parseFloat(s);
        } catch (Exception e) {
            return 0f;
        }
    }
}
