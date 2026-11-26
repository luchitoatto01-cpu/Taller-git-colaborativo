package pe.edu.upeu.perfumeria.utils;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;

import java.util.Locale;

public class UtilTabla {

    /** Convierte una columna en la columna correlativa 1, 2, 3... */
    public static <T> void columnaNumerada(TableColumn<T, Number> columna) {
        columna.setSortable(false);
        columna.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : String.valueOf(getIndex() + 1));
            }
        });
    }

    public static String soles(double monto) {
        return String.format(Locale.US, "S/ %.2f", monto);
    }
}
