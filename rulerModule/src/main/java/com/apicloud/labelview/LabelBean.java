package com.apicloud.labelview;

import java.util.List;

public class LabelBean {
    String title;
    Object picString;
    List<Bean> labelList;

    static class Bean {
        float startX;
        float startY;
        float endX;
        float endY;
        String labelText;

        public Bean(float startX, float startY, float endX, float endY, String labelText) {
            this.startX = startX;
            this.endX = endX;
            this.startY = startY;
            this.endY = endY;
            this.labelText = labelText;
        }
    }
}
