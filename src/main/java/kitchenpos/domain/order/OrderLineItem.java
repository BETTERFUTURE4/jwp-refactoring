package kitchenpos.domain.order;

public class OrderLineItem {
    private final Long seq;
    private Long orderId;
    private final Long menuId;
    private final long quantity;

    public OrderLineItem(Long seq, Long orderId, Long menuId, long quantity) {
        this.seq = seq;
        this.orderId = orderId;
        this.menuId = menuId;
        this.quantity = quantity;
    }

    public OrderLineItem(Long menuId, long quantity) {
        this(null, null, menuId, quantity);
    }

    public OrderLineItem placeOrderId(final Long orderId) {
        this.orderId = orderId;
        return this;
    }

    public Long getSeq() {
        return seq;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getMenuId() {
        return menuId;
    }

    public long getQuantity() {
        return quantity;
    }
}