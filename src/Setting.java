import org.nocrala.tools.texttablefmt.BorderStyle;
import org.nocrala.tools.texttablefmt.CellStyle;
import org.nocrala.tools.texttablefmt.ShownBorders;
import org.nocrala.tools.texttablefmt.Table;

import java.io.Serializable;

class Setting implements Serializable {
    public boolean recovery;
    public boolean autoSave;

    public Setting() {
        recovery = true;
        autoSave = false;
    }

    @Override
    public String toString() {
        Table table = new Table(1, BorderStyle.UNICODE_DOUBLE_BOX, ShownBorders.SURROUND_HEADER_AND_COLUMNS);
        table.setColumnWidth(0, 30, 30);
        table.addCell("Setting", new CellStyle(CellStyle.HorizontalAlign.center));
        table.addCell("Recovery: " + recovery);
        table.addCell("Auto Save: " + autoSave);
        return table.render();
    }
}
