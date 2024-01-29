package com.yhb.fastocr;

/**FastOcr文字识别回调接口*/
public interface FastOcrTextResult {
    /**
     * @param result 成功/失败
     * @param language 识别使用的语言
     * @param text 识别出的文字
     */
    void fastOcrText(boolean result, String language, String text);
}