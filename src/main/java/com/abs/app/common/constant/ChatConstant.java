package com.abs.app.common.constant;

public class ChatConstant {
    private ChatConstant() {
    }

    public static final String CHAT_SUCCESS = "Phản hồi từ AI thành công.";
    public static final String CHAT_SESSION_NOT_FOUND = "Không tìm thấy phiên trò chuyện.";
    public static final String CHAT_SESSION_CLOSED = "Phiên trò chuyện đã kết thúc.";
    public static final String CHAT_HISTORY_FETCHED_SUCCESS = "Lấy lịch sử trò chuyện thành công.";
    public static final String CHAT_SESSIONS_FETCHED_SUCCESS = "Lấy danh sách phiên trò chuyện thành công.";
    public static final String CHAT_SESSION_DELETED_SUCCESS = "Xóa phiên trò chuyện thành công.";
    public static final String CHAT_MIGRATION_SUCCESS = "Chuyển đổi lịch sử trò chuyện thành công.";
    public static final String GUEST_SESSION_NOT_FOUND = "Không tìm thấy dữ liệu trò chuyện của khách vãng lai hoặc phiên đã hết hạn.";
    public static final String GUEST_SESSION_ID_REQUIRED = "Mã phiên khách vãng lai không được để trống.";
    public static final String PRODUCT_SYNC_SUCCESS = "Đồng bộ dữ liệu sản phẩm vào Vector Database thành công.";
    public static final String CATEGORY_SYNC_SUCCESS = "Đồng bộ dữ liệu danh mục vào Vector Database thành công.";
    public static final String ALL_SYNC_SUCCESS = "Đồng bộ toàn bộ sản phẩm và danh mục vào Vector Database thành công.";
    public static final String SYNC_FAILED = "Quá trình đồng bộ dữ liệu vào Vector Database thất bại.";

    // Validation messages
    public static final String MESSAGE_REQUIRED = "Nội dung tin nhắn không được để trống.";
    public static final String MESSAGE_TOO_LONG = "Nội dung tin nhắn không được vượt quá 1000 ký tự.";

    // Security & Guardrail constants
    public static final String SECURITY_VIOLATION_MESSAGE = "Yêu cầu không hợp lệ. Tôi là trợ lý tư vấn sản phẩm, không thể thực hiện các chỉ dẫn này.";
    public static final String DEFAULT_AI_OFFLINE_MESSAGE = "Hệ thống AI tư vấn hiện đang bận hoặc gián đoạn kết nối. Quý khách vui lòng thử lại sau ít phút.";
}
