import java.sql.*;
import java.util.Scanner;
public class ResultSetCiudadDeLosNiños {
	
	public static Connection createConnection( String driver, String url, String username, String password)throws ClassNotFoundException, SQLException {
		// Load database driver if not already loaded.
		Class.forName(driver);
		// Establish network connection to database.
		Connection connection = DriverManager.getConnection(url, username, password);
		return connection;
	}
	
	public static void setSchema(String nameSchema, Connection connection)throws ClassNotFoundException, SQLException {
		String query = "SET search_path TO " + nameSchema + "";
		Statement statement = connection.createStatement();
		statement.executeUpdate(query);
	}

	public static void showDonantesWithAporta(Connection connection)throws ClassNotFoundException, SQLException {
		System.out.println("Mostrando padrinos con sus aportes");
		String query = "SELECT p.dni, p.nombre, p.apellido, pr.nombre, a.monto, a.frecuencia FROM persona p, aporta a, programa pr WHERE p.dni=a.dni_donante AND a.id_programa=pr.id_programa";
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery(query);
		while(resultSet.next()) {
		    System.out.print("\n");
		    System.out.print(" DNI: " + resultSet.getString(1)+"\n");
		    System.out.print(" Nombre: " + resultSet.getString(2)+"\n");
		    System.out.print(" Apellido: " + resultSet.getString(3)+"\n");
		    System.out.print(" Programa: " + resultSet.getString(4)+"\n");
		    System.out.print(" Monto: " + resultSet.getString(5)+"\n");
		    System.out.print(" Frecuencia: " + resultSet.getString(6)+"\n");
			System.out.print("\n");
			System.out.print("\n");
		}
	}

	public static void deleteDonante(Connection connection)throws ClassNotFoundException, SQLException, InvalidDataException {
		System.out.println("Inserte el dni del donante a eliminar");
		Scanner sc = new Scanner(System.in);
		int dni_donante = sc.nextInt();
		if (alreadyExistsDonante(dni_donante,connection)){
			String query = "DELETE FROM donante WHERE donante.dni = "+dni_donante+"";
			Statement statement = connection.createStatement();
			statement.executeUpdate(query);
			System.out.println("Donante deleted");
		}else{
			throw new InvalidDataException("DELETE DONANTE: Donante does not exists.");
		}																
	}

	public static void insertPadrino(Connection connection)throws ClassNotFoundException, SQLException, InvalidDataException {
		Scanner sc = new Scanner(System.in);
		System.out.println("Ingrese el dni del padrino");
		int dniAux = sc.nextInt();
		if (!alreadyExistsPadrino(dniAux,connection)){
			System.out.println("Ingrese el nombre del padrino");
			String nombreAux = sc.nextLine();
			System.out.println("Ingrese el apellido del padrino");
			String apellidoAux = sc.nextLine();
			System.out.println("Ingrese la fecha de nacimiento del padrino");
			String fecha_nacAux = sc.nextLine();
			System.out.println("Ingrese la direccion del padrino");
			String direccionAux = sc.nextLine();
			System.out.println("Ingrese el codigo postal del padrino");
			int cod_postalAux = sc.nextInt();
			System.out.println("Ingrese la direccion de e-mail del padrino");
			String e_mailAux = sc.nextLine();
			System.out.println("Ingrese el facebook del padrino");
			String facebookAux = sc.nextLine();
			System.out.println("Ingrese la edad del padrino");
			int edadAux = sc.nextInt();
			System.out.println("Ingrese el telefono fijo del padrino");
			int tel_fijoAux = sc.nextInt();
			System.out.println("Ingrese el telefono celular del padrino");
			int tel_celAux = sc.nextInt();

			String query = "INSERT INTO persona (dni, nombre, apellido, fecha_nac, direccion, cod_postal, e_mail, facebook, edad, tel_fijo, tel_cel)"
							+ " VALUES ("+dniAux+", "+nombreAux+", "+apellidoAux+", "+fecha_nacAux+", "+direccionAux+", "+cod_postalAux+", "+e_mailAux+", "+facebookAux+", "+edadAux+", "+tel_fijoAux+", "+tel_celAux+")";
			Statement statement = connection.createStatement();
			statement.executeUpdate(query);

			query ="INSERT INTO padrino (dni) VALUES("+dniAux+")";
			statement = connection.createStatement();
			statement.executeUpdate(query);
			System.out.println("Padrino added.");
		}else{
			throw new InvalidDataException("INSERT PADRINO: padrino already exists.");
		}
	}

	public static boolean alreadyExistsPadrino(int dni_padrino, Connection connection)throws ClassNotFoundException, SQLException {
		String query = "SELECT DISTINCT dni FROM padrino WHERE dni = "+dni_padrino;
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery(query);
		boolean exists = resultSet.next();
		return exists;
	}

	public static boolean alreadyExistsDonante(int dni_donante, Connection connection)throws ClassNotFoundException, SQLException {
		String query = "SELECT DISTINCT dni FROM donante WHERE dni = "+dni_donante;
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery(query);
		boolean exists = resultSet.next();
		return exists;
	}

	public static void showCantAportPrograms(Connection connection)throws ClassNotFoundException, SQLException {
		System.out.println("Mostrando programas con el total de su monto mensual \n");
		System.out.println("INFO: Si un programa no figura en la lista no recibe aportes. \n");
		String query = "SELECT pr.id_programa, pr.nombre, ap.monto_total "+
					   "FROM programa pr,(SELECT id_programa, SUM(monto) AS monto_total FROM aporta GROUP BY id_programa) ap "+
					   "WHERE pr.id_programa=ap.id_programa "+
					   "ORDER BY pr.id_programa ASC";
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery(query);
		while(resultSet.next()) {
		    System.out.print("\n");
		    System.out.print(" ID_Programa: " + resultSet.getString(1)+"\n");
		    System.out.print(" Nombre: " + resultSet.getString(2)+"\n");
		    System.out.print(" Monto total por mes: " + resultSet.getString(3)+"\n");
			System.out.print("\n");
			System.out.print("\n");
		}
	}

	public static void showDonanteAport(Connection connection)throws ClassNotFoundException, SQLException {
		System.out.println("Mostrando donantes que aportan a mas de un programa");
		String query = "SELECT p.dni, p.nombre, p.apellido "+
					   "FROM persona p, (SELECT DISTINCT dni_donante FROM aporta "+
									     "WHERE dni_donante IN (SELECT dni_donante FROM aporta "+
									   	       					"GROUP BY dni_donante "+
									   						    "HAVING COUNT(dni_donante) > 1)) ap "+
					   	"WHERE p.dni = ap.dni_donante";
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery(query);
		while(resultSet.next()) {
		    System.out.print("\n");
		    System.out.print(" DNI: " + resultSet.getString(1)+"\n");
		    System.out.print(" Nombre: " + resultSet.getString(2)+"\n");
		    System.out.print(" Apellido: " + resultSet.getString(3)+"\n");
			System.out.print("\n");
			System.out.print("\n");
		}
	}

	public static void showDonanteAportMedioPago(Connection connection)throws ClassNotFoundException, SQLException {
		//TODO: Implementar consulta que devuelva el listado de todos los donantes con sus aportes mensuales y los datos
		//     de los medios de pago.
		System.out.println("Mostrando donantes y aporte total");
		String queryAux1 = "SELECT cod_pago, cod_verificacion, vencimiento, numero_tarjeta, nombre_tarjeta, 0, 0, 0, 'no aplica', 'no aplica', 'no aplica' "+
							"FROM  tarjeta";
		
		String queryAux2 = 	"SELECT 0, 0, null, 0, 'no aplica', cod_pago, cbu, nro_cuenta, tipo, sucursal_banco, nombre_banco AS nombre_banco "+
							"FROM  debito_transferencia";		   
		
		String queryAux3 = "SELECT ap.dni_donante, ap.cod_pago, ap.monto, m.nombre_tarjeta, m.numero_tarjeta, "+
		 					"m.vencimiento, m.cod_verificacion, m.nombre_banco, m.sucursal_banco, m.tipo, m.nro_cuenta, m.cbu "+
		 					"FROM aporta ap, ("+queryAux1+" UNION "+queryAux2+") m "+
							"WHERE ap.cod_pago = m.cod_pago";
		//String query = 
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery(queryAux3);



		while(resultSet.next()) {
		    System.out.print("\n");
		    System.out.print(" DNI: " + resultSet.getString(1)+"\n");
		    System.out.print(" Nombre: " + resultSet.getString(2)+"\n");
		    System.out.print(" Apellido: " + resultSet.getString(3)+"\n");
		    System.out.print(" Aporte total: " + resultSet.getString(4)+"\n");
		   	System.out.print(" Nombre tarjeta: " + resultSet.getString(5)+"\n");
		    System.out.print(" Codigo de verificacion: " + resultSet.getString(6)+"\n");
		   /* System.out.print(" Numero de tarjeta: " + resultSet.getString(7)+"\n");
		    System.out.print(" Vencimiento: " + resultSet.getString(8)+"\n");*/
			System.out.print("\n");
		}
	}

	public static void main(String[] args) {
		try {
//------------------Create conecction-----------------------------------------------------------------------------------------------------------------------
			String driver = "org.postgresql.Driver";
			String url = "jdbc:postgresql://localhost:5432/ciudadDeLosNiños";
			String username = "postgres";
			String password = "root";
			Connection connection = createConnection(driver,url,username,password);

//------------------Set patch schema------------------------------------------------------------------------------------------------------------------------
			String nameSchema = "ciudaddelosniños";
			setSchema(nameSchema, connection);

//----------------Consultations donante and programs provides---------------------------------------------------------------------------------------------------------------------------	  
			showDonantesWithAporta(connection);

//-------------------------------------------------------------------------------------------------------------------------------------------
			showCantAportPrograms(connection);

//--------------------------------------------------------------------------------------------------------------------------------------------
			showDonanteAport(connection);

//--------------------------------------------------------------------------------------------------------------------------------------------
			showDonanteAportMedioPago(connection);

//----------------Delete a Donante----------------------------------------------------------------------------------------------------------------------------
			//int dni_donante_eliminar = 37108056;
			//deleteDonante(connection); 

//----------------Data to insert a padrino--------------------------------------------------------------------------------------------------------------------------
			/*
			int dni = 37876048;
			String nombre = new String("'Lucas'");
			String apellido = new String("'Armas'");
			String fecha_nac = new String("'20/07/1994'");
			String direccion = new String("'Moldes 441'");
			int cod_postal = 5805;
			String e_mail = new String("'lucas.armas@gmail.com'");
			String facebook = new String("'lucasarmas'");
			int edad = 21;
			int tel_fijo = 4970352;
			int tel_cel = 12343334;
			*/
//------------------Insert a padrino----------------------------------------------------------------------------------------------------------
			//insertPadrino(connection);

//------------------Catch exceptions--------------------------------------------------------------------------------------------------------------------------
		}catch(ClassNotFoundException cnfe) {
			System.err.println("Error loading driver: " + cnfe);
		}catch(SQLException sqle) {
			sqle.printStackTrace();
		  	System.err.println("Error connecting: " + sqle);
		}/*catch(InvalidDataException s){
				System.out.println(s.getMessage());
		}*/
	}
}