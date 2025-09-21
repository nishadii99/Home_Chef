package lk.ijse.backend.model;

import lk.ijse.backend.dto.ItemDTO;
import lk.ijse.backend.dto.UserDTO;

import java.util.List;

public class CartDataModel {
    private UserDTO customerDTO;
    private List<ItemDTO> itemDTOS;

    public CartDataModel() {
    }

    public CartDataModel(UserDTO customerDTO, List<ItemDTO> itemDTOS) {
        this.customerDTO = customerDTO;
        this.itemDTOS = itemDTOS;
    }

    public UserDTO getCustomerDTO() {
        return customerDTO;
    }

    public void setCustomerDTO(UserDTO customerDTO) {
        this.customerDTO = customerDTO;
    }

    public List<ItemDTO> getItemDTOS() {
        return itemDTOS;
    }

    public void setItemDTOS(List<ItemDTO> itemDTOS) {
        this.itemDTOS = itemDTOS;
    }

    @Override
    public String toString() {
        return "CartDataModel{" +
                "customerDTO=" + customerDTO +
                ", itemDTOS=" + itemDTOS +
                '}';
    }
}
