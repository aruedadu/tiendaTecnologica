package dominio;

import dominio.repositorio.RepositorioProducto;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import dominio.excepcion.GarantiaExtendidaException;
import dominio.repositorio.RepositorioGarantiaExtendida;

public class Vendedor {

	public static final String EL_PRODUCTO_TIENE_GARANTIA = "El producto ya cuenta con una garantia extendida";

	// nueva constante para la garantia extendida
	public static final String PRODUCTO_NO_PERMITE_GARANTIA = "Este producto no cuenta con garantia extendida";

	private RepositorioProducto repositorioProducto;
	private RepositorioGarantiaExtendida repositorioGarantia;

	public Vendedor(RepositorioProducto repositorioProducto, RepositorioGarantiaExtendida repositorioGarantia) {
		this.repositorioProducto = repositorioProducto;
		this.repositorioGarantia = repositorioGarantia;

	}

	public void generarGarantia(String codigo, String clientName) {

		if (tieneGarantia(codigo))
			throw new GarantiaExtendidaException(EL_PRODUCTO_TIENE_GARANTIA);

		if (!allowsWarranty(codigo))
			throw new GarantiaExtendidaException(PRODUCTO_NO_PERMITE_GARANTIA);

		Producto product = repositorioProducto.obtenerPorCodigo(codigo);

		double warrantyPrice = getWarrantyPrice(product.getPrecio());
		int warrantyDuration = getWarrantyDuration(product.getPrecio());

		LocalDate requestDate = LocalDate.now();
		LocalDate warrantyEndDate = getDateEndWarranty(requestDate, warrantyDuration);

		GarantiaExtendida extendedWarranty = new GarantiaExtendida(product, convertLocalToDate(requestDate),
				convertLocalToDate(warrantyEndDate), warrantyPrice, clientName);

		repositorioGarantia.agregar(extendedWarranty);

	}

	public boolean tieneGarantia(String codigo) {
		Producto producto = repositorioGarantia.obtenerProductoConGarantiaPorCodigo(codigo);
		return producto != null;
	}

	/**
	 * Metodo que verifica la cantidad de vocales para definir si permite o no
	 * garatia
	 * 
	 * @param codigo
	 * @return V/F si permite o no garaia
	 */
	public boolean allowsWarranty(String codigo) {
		return getNumberOfVowels(codigo) == 3 ? false : true;
	}

	/**
	 * Metodo para contar
	 * 
	 * @param text
	 * @return cantidad de vocales en un texto
	 */
	public int getNumberOfVowels(String text) {
		return (int) text.toUpperCase().chars().mapToObj(i -> (char) i).filter(c -> "AEIOU".contains(String.valueOf(c)))
				.count();
	}

	/**
	 * Metodo para calcular el costo de la garantia extendida para un producto
	 * 
	 * @param price
	 * @return
	 */
	public double getWarrantyPrice(double price) {
		return price > 500000 ? price * 0.2 : price * 0.1;
	}

	/**
	 * Metodo para calcular la cantidad de dias que dura la garantia
	 * 
	 * @param price
	 * @return cantidad de dias que dura la garantia
	 */
	public int getWarrantyDuration(double price) {
		return price > 500000 ? 200 : 100;
	}

	/**
	 * 
	 * @param requestDate
	 * @param warrantyDuration
	 * @return fecha en la que termina la garantia
	 */
	public LocalDate getDateEndWarranty(LocalDate requestDate, int warrantyDuration) {
		LocalDate warrantyEnd = requestDate.plusDays(warrantyDuration);
		if (warrantyDuration == 200) {
			while (warrantyEnd.isAfter(requestDate)) {
				if (DayOfWeek.MONDAY.equals(requestDate.getDayOfWeek())) {
					warrantyEnd = warrantyEnd.plusDays(1);
				}
				requestDate = requestDate.plusDays(1);
			}
			if (warrantyEnd.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
				warrantyEnd = warrantyEnd.plusDays(1);
			}
		}
		return warrantyEnd;
	}

	public Date convertLocalToDate(LocalDate dateToConvert) {
		return java.util.Date.from(dateToConvert.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
	}

}
