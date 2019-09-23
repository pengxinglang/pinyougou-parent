package entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@Data
public class PageResult implements Serializable {

    private long total;
    private List rows;

}
