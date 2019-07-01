package dominio.unitaria;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.Month;

import org.junit.Assert;
import org.junit.Test;

import dominio.Vendedor;
import dominio.Producto;
import dominio.repositorio.RepositorioProducto;
import dominio.repositorio.RepositorioGarantiaExtendida;
import testdatabuilder.ProductoTestDataBuilder;

public class VendedorTest {

	private static final String NO_WARRANTY_CODE_3_VOWELS = "CEIBA20190701";
	private static final String NO_WARRANTY_CODE_5_VOWELS = "CEIBAOU20190701";
	private static final String PRODUCT_NAME = "Celular huawey";

	@Test
	public void productoYaTieneGarantiaTest() {

		// arrange
		ProductoTestDataBuilder productoTestDataBuilder = new ProductoTestDataBuilder();

		Producto producto = productoTestDataBuilder.build();

		RepositorioGarantiaExtendida repositorioGarantia = mock(RepositorioGarantiaExtendida.class);
		RepositorioProducto repositorioProducto = mock(RepositorioProducto.class);

		when(repositorioGarantia.obtenerProductoConGarantiaPorCodigo(producto.getCodigo())).thenReturn(producto);

		Vendedor vendedor = new Vendedor(repositorioProducto, repositorioGarantia);

		// act
		boolean existeProducto = vendedor.tieneGarantia(producto.getCodigo());

		// assert
		assertTrue(existeProducto);
	}

	@Test
	public void productoNoTieneGarantiaTest() {

		// arrange
		ProductoTestDataBuilder productoestDataBuilder = new ProductoTestDataBuilder();

		Producto producto = productoestDataBuilder.build();

		RepositorioGarantiaExtendida repositorioGarantia = mock(RepositorioGarantiaExtendida.class);
		RepositorioProducto repositorioProducto = mock(RepositorioProducto.class);

		when(repositorioGarantia.obtenerProductoConGarantiaPorCodigo(producto.getCodigo())).thenReturn(null);

		Vendedor vendedor = new Vendedor(repositorioProducto, repositorioGarantia);

		// act
		boolean existeProducto = vendedor.tieneGarantia(producto.getCodigo());

		// assert
		assertFalse(existeProducto);
	}

	// PRUEBAS DE NUEVO DESARROLLO

	@Test
	public void validateVowels3Test() {

		// arrange
		String productCode = NO_WARRANTY_CODE_3_VOWELS;
		RepositorioGarantiaExtendida warrantyRepo = mock(RepositorioGarantiaExtendida.class);
		RepositorioProducto productRepo = mock(RepositorioProducto.class);

		Vendedor salesman = new Vendedor(productRepo, warrantyRepo);

		// act
		int codeVowels = salesman.getNumberOfVowels(productCode);
		int expectedVowels = 3;

		// assert
		Assert.assertEquals(codeVowels, expectedVowels);
	}

	@Test
	public void validateVowels5Test() {

		// arrange
		String productCode = NO_WARRANTY_CODE_5_VOWELS;
		RepositorioGarantiaExtendida warrantyRepo = mock(RepositorioGarantiaExtendida.class);
		RepositorioProducto productRepo = mock(RepositorioProducto.class);
		Vendedor salesman = new Vendedor(productRepo, warrantyRepo);

		// act
		int codeVowels = salesman.getNumberOfVowels(productCode);
		int expectedVowels = 5;

		// assert
		Assert.assertEquals(codeVowels, expectedVowels);
	}

	@Test
	public void validateWarrantyEndTest() {
		// arrange
		Producto product = new ProductoTestDataBuilder().conNombre(PRODUCT_NAME).conPrecio(640000l).build();
		LocalDate requestDate = LocalDate.of(2018, Month.AUGUST, 16);
		RepositorioGarantiaExtendida warrantyRepository = mock(RepositorioGarantiaExtendida.class);
		RepositorioProducto productRepository = mock(RepositorioProducto.class);
		Vendedor salesman = new Vendedor(productRepository, warrantyRepository);

		// act
		int warrantyDuration = salesman.getWarrantyDuration(product.getPrecio());
		LocalDate warrantyEnd = salesman.getDateEndWarranty(requestDate, warrantyDuration);
		LocalDate expectedEnd = LocalDate.of(2019, Month.APRIL, 6);

		// assert
		Assert.assertEquals(expectedEnd, warrantyEnd);
	}

	@Test
	public void validateSundayTest() {
		// arrange
		Producto producto = new ProductoTestDataBuilder().conNombre(PRODUCT_NAME).conPrecio(640000l).build();
		LocalDate fechaSolicitud = LocalDate.of(2018, Month.AUGUST, 17);
		RepositorioGarantiaExtendida repositorioGarantia = mock(RepositorioGarantiaExtendida.class);
		RepositorioProducto repositorioProducto = mock(RepositorioProducto.class);
		Vendedor vendedor = new Vendedor(repositorioProducto, repositorioGarantia);

		// act
		int diasGarantia = vendedor.getWarrantyDuration(producto.getPrecio());
		LocalDate fechaFinGarantia = vendedor.getDateEndWarranty(fechaSolicitud, diasGarantia);
		LocalDate fechaFinGarantiaEsperada = LocalDate.of(2019, Month.APRIL, 8);

		// assert
		Assert.assertEquals(fechaFinGarantiaEsperada, fechaFinGarantia);
	}
}
