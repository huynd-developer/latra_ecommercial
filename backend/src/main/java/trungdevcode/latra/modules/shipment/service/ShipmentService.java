// backend/src/main/java/trungdevcode/latra/modules/shipment/service/ShipmentService.java
package trungdevcode.latra.modules.shipment.service;

import trungdevcode.latra.entity.Shipment;
import trungdevcode.latra.modules.shipment.dto.request.UpdateShipmentRequestDTO;

public interface ShipmentService {
    Shipment getShipmentByOrderId(Long orderId);
    Shipment updateTrackingInfo(Long shipmentId, UpdateShipmentRequestDTO request);
    Shipment updateShippingAddress(Long orderId, String newName, String newPhone, String newAddress);
}