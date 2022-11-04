package kitchenpos.order.ui;

import java.net.URI;
import java.util.List;
import kitchenpos.order.application.TableService;
import kitchenpos.order.dto.OrderTableChangeEmptyRequest;
import kitchenpos.order.dto.OrderTableCreateRequest;
import kitchenpos.order.dto.OrderTableResponse;
import kitchenpos.order.dto.TableGuestChangeRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TableRestController {
    private final TableService tableService;

    public TableRestController(final TableService tableService) {
        this.tableService = tableService;
    }

    @PostMapping("/api/tables")
    public ResponseEntity<OrderTableResponse> create(@RequestBody final OrderTableCreateRequest orderTable) {
        final OrderTableResponse created = tableService.create(orderTable);
        final URI uri = URI.create("/api/tables/" + created.getId());
        return ResponseEntity.created(uri).body(created);
    }

    @GetMapping("/api/tables")
    public ResponseEntity<List<OrderTableResponse>> list() {
        return ResponseEntity.ok().body(tableService.list());
    }

    @PutMapping("/api/tables/{orderTableId}/empty")
    public ResponseEntity<OrderTableResponse> changeEmpty(@PathVariable final Long orderTableId,
                                                          @RequestBody final OrderTableChangeEmptyRequest request) {
        OrderTableResponse table = tableService.changeEmpty(orderTableId, request);
        return ResponseEntity.ok().body(table);
    }

    @PutMapping("/api/tables/{orderTableId}/number-of-guests")
    public ResponseEntity<OrderTableResponse> changeNumberOfGuests(
            @PathVariable final Long orderTableId,
            @RequestBody final TableGuestChangeRequest orderTable) {
        return ResponseEntity.ok().body(tableService.changeNumberOfGuests(orderTableId, orderTable));
    }
}