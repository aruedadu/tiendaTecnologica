package dominio.integracion;

import static org.junit.Assert.fail;

import java.time.LocalDate;
import java.time.Month;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import dominio.GarantiaExtendida;
import dominio.Producto;
import dominio.Vendedor;
import dominio.excepcion.GarantiaExtendidaException;
import dominio.repositorio.RepositorioGarantiaExtendida;
import dominio.repositorio.RepositorioProducto;
import persistencia.sistema.SistemaDePersistencia;
import testdatabuilder.ProductoTestDataBuilder;

public class VendedorTest {

	private static final String COMPUTADOR_LENOVO = "Computador Lenovo";
	private static final String CLIENT_NAME = "ALEJANDRO RUEDA DUARTE";
	private static final String NO_WARRANTY_CODE = "CEIBA20190701";
	private static final String CAMARA_SAMSUNG = "Camara Samsung";
	private static final String TECLADO_APPLE = "Teclado Apple";

	private SistemaDePersistencia sistemaPersistencia;

	private RepositorioProducto repositorioProducto;
	private RepositorioGarantiaExtendida repositorioGarantia;

	@Before
	public void setUp() {

		sistemaPersistencia = new SistemaDePersistencia();

		repositorioProducto = sistemaPersistencia.obtenerRepositorioProductos();
		repositorioGarantia = sistemaPersistencia.obtenerRepositorioGarantia();

		sistemaPersistencia.iniciar();
	}

	@After
	public void tearDown() {
		sistemaPersistencia.terminar();
	}

	@Test
	public void generarGarantiaTest() {

		// arrange
		Producto producto = new ProductoTestDataBuilder().conNombre(COMPUTADOR_LENOVO).build();
		repositorioProducto.agregar(producto);
		Vendedor vendedor = new Vendedor(repositorioProducto, repositorioGarantia);

		// act
		vendedor.generarGarantia(producto.getCodigo(), CLIENT_NAME);

		// assert
		Assert.assertTrue(vendedor.tieneGarantia(producto.getCodigo()));
		Assert.assertNotNull(repositorioGarantia.obtenerProductoConGarantiaPorCodigo(producto.getCodigo()));

	}

	@Test
	public void productoYaTieneGarantiaTest() {

		// arrange
		Producto producto = new ProductoTestDataBuilder().conNombre(COMPUTADOR_LENOVO).build();
		repositorioProducto.agregar(producto);
		Vendedor vendedor = new Vendedor(repositorioProducto, repositorioGarantia);

		// act
		vendedor.generarGarantia(producto.getCodigo(), CLIENT_NAME);
		try {
			vendedor.generarGarantia(producto.getCodigo(), CLIENT_NAME);
			fail();
		} catch (GarantiaExtendidaException e) {
			// assert
			Assert.assertEquals(Vendedor.EL_PRODUCTO_TIENE_GARANTIA, e.getMessage());
		}
	}

	// NUEVAS PRUEBAS

	/**
	 * Prueba para producto que no permite garantia
	 */
	@Test
	public void noWarrantyProductTest() {
		// arrange
		Producto product = new ProductoTestDataBuilder().conNombre(COMPUTADOR_LENOVO).conCodigo(NO_WARRANTY_CODE)
				.build();
		repositorioProducto.agregar(product);
		Vendedor salesman = new Vendedor(repositorioProducto, repositorioGarantia);

		// act
		try {
			salesman.generarGarantia(product.getCodigo(), CLIENT_NAME);
			fail();
		} catch (GarantiaExtendidaException e) {
			// assert
			Assert.assertEquals(Vendedor.PRODUCTO_NO_PERMITE_GARANTIA, e.getMessage());
		}
	}

	/**
	 * Prueba de generacion de garantia para un producto que cuete mas de 500000
	 */
	@Test
	public void checkWarrantyGreaterThanTest() {

		// arrange
		Producto product = new ProductoTestDataBuilder().conNombre(COMPUTADOR_LENOVO).conPrecio(2500000l).build();
		repositorioProducto.agregar(product);
		Vendedor salesman = new Vendedor(repositorioProducto, repositorioGarantia);

		// act
		salesman.generarGarantia(product.getCodigo(), CLIENT_NAME);
		GarantiaExtendida warranty = repositorioGarantia.obtener(product.getCodigo());
		long expectedValue = 500000l;

		System.err.println("fecha fin garantia para mayor a 500 " + warranty.getFechaFinGarantia());

		// assert
		Assert.assertEquals(expectedValue, warranty.getPrecioGarantia(), 0);
	}

	/**
	 * Preba de generacion de garantia para producto con coste igual a 500000
	 */
	@Test
	public void checkWarrantyEqualsTest() {

		// arrange
		Producto product = new ProductoTestDataBuilder().conNombre(CAMARA_SAMSUNG).conPrecio(500000l).build();
		repositorioProducto.agregar(product);
		Vendedor salesman = new Vendedor(repositorioProducto, repositorioGarantia);

		// act
		salesman.generarGarantia(product.getCodigo(), CLIENT_NAME);
		GarantiaExtendida warranty = repositorioGarantia.obtener(product.getCodigo());
		long expectedValue = 50000l;

		System.err.println("fecha fin garantia para igual a 500 " + warranty.getFechaFinGarantia());

		// assert
		Assert.assertEquals(expectedValue, warranty.getPrecioGarantia(), 0);
	}

	/**
	 * Prueba de garantia extendida para un producto que cueste menos de 500000
	 */
	@Test
	public void checkWarrantyLessThanTest() {

		// arrange
		Producto product = new ProductoTestDataBuilder().conNombre(TECLADO_APPLE).conPrecio(300000l).build();
		repositorioProducto.agregar(product);
		Vendedor salesman = new Vendedor(repositorioProducto, repositorioGarantia);

		// act
		salesman.generarGarantia(product.getCodigo(), CLIENT_NAME);
		GarantiaExtendida warranty = repositorioGarantia.obtener(product.getCodigo());
		long valorGarantiaEsperado = 30000l;

		// assert
		Assert.assertEquals(valorGarantiaEsperado, warranty.getPrecioGarantia(), 9);

	}

	/**
	 * Prueba de duracion de garantia para un producto con precio superior a 500000
	 */
	@Test
	public void checkWarrantyDurationGreaterTTest() {

		// arrange
		Producto producto = new ProductoTestDataBuilder().conNombre(COMPUTADOR_LENOVO).conPrecio(2500000l).build();
		repositorioProducto.agregar(producto);
		Vendedor vendedor = new Vendedor(repositorioProducto, repositorioGarantia);

		// act
		int diasGarantia = vendedor.getWarrantyDuration(producto.getPrecio());
		int diasGarantiaEsperados = 200;

		// assert
		Assert.assertEquals(diasGarantiaEsperados, diasGarantia);
	}

	/**
	 * Prueba de duracion de garantia para un producto que cuesta 500000
	 */
	@Test
	public void checkWarrantyDurationEqualsTest() {

		// arrange
		Producto producto = new ProductoTestDataBuilder().conNombre(CAMARA_SAMSUNG).conPrecio(500000l).build();

		repositorioProducto.agregar(producto);
		Vendedor vendedor = new Vendedor(repositorioProducto, repositorioGarantia);

		// act
		int diasGarantia = vendedor.getWarrantyDuration(producto.getPrecio());
		int diasGarantiaEsperados = 100;

		// assert
		Assert.assertEquals(diasGarantiaEsperados, diasGarantia);
	}

	/**
	 * Prueba de duracion de gatantia para un producto que cueste menos de 500000
	 */
	@Test
	public void checkWarrantyDurationLessthenTest() {

		// arrange
		Producto producto = new ProductoTestDataBuilder().conNombre(TECLADO_APPLE).conPrecio(300000l).build();

		repositorioProducto.agregar(producto);
		Vendedor vendedor = new Vendedor(repositorioProducto, repositorioGarantia);

		// act
		int diasGarantia = vendedor.getWarrantyDuration(producto.getPrecio());
		int diasGarantiaEsperados = 100;

		// assert
		Assert.assertEquals(diasGarantiaEsperados, diasGarantia);
	}

	/**
	 * Verificacion de almacenado correcto
	 */
	@Test
	public void checkSavedDataTest() {

		// arrange
		Producto product = new ProductoTestDataBuilder().conNombre(COMPUTADOR_LENOVO).build();
		repositorioProducto.agregar(product);
		Vendedor salesman = new Vendedor(repositorioProducto, repositorioGarantia);

		// act
		salesman.generarGarantia(product.getCodigo(), CLIENT_NAME);
		GarantiaExtendida warranty = repositorioGarantia.obtener(product.getCodigo());

		Assert.assertNotNull(warranty.getProducto());
		Assert.assertNotNull(warranty.getNombreCliente());
		Assert.assertNotNull(warranty.getPrecioGarantia());
		Assert.assertNotNull(warranty.getFechaSolicitudGarantia());
		Assert.assertNotNull(warranty.getFechaFinGarantia());
	}
}
