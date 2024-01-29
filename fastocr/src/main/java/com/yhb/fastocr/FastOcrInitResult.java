package com.yhb.fastocr;

/**FastOcr初始化回调接口*/
public interface FastOcrInitResult {
    /**
     * @param fastOcr 不为空则初始化成功，为空则失败
     * @param msg 初始化消息
     */
    void faseOcrInit(FastOcr fastOcr, String msg);
}