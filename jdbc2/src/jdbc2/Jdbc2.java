package jdbc2;
//Silvia

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.regex.*;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Jdbc2 {

	/**
	 * 
	 */
	private JFrame frame;
	//Tablas
	private JTable usuariotable;
	private JTable bicitable;
	//Usuario
	private JTextField idUsuariotextField;
	private JTextField nombretextField;
	private JTextField edadtextField;
	private JTextField cuentaBancariatextField;
	//Bici
	private JTextField idbicitextField;
	private JTextField disponibilidadtextField;
	private JTextField valoraciontextField;
	private JTextField fechaIniciotextField;
	private JTextField fechaFintextField;
	private JTextField usuarioidtextField;
	private JTextField textFieldimporte;
	private JTextField usuarioIdtextField;	

	//Botones
	private JButton crearBicibtn;
	private JButton actualizarBicibtn;
	private JButton mostrarBicibtn;
	private JButton borrarBicibtn;
	private JButton alquilarBicibtn;
	private JButton devolverBicibtn;
	//Labels
	private JLabel lblNewLabel;
	private JLabel lblNewLabel_1;
	private JLabel lblNewLabel_2;
	private JLabel lblNewLabel_3;
	private JLabel lblNewLabel_4;
	private JLabel lblNewLabel_5;
	private JLabel lblNewLabel_6;
	private JLabel lblNewLabel_7;
	private JLabel lblNewLabel_8;
	private JLabel lblNewLabel_14;
	private JLabel lblNewLabel_15;

	private static String CUENTABANCARIA = "^ES\\d{2}-\\d{4}-\\d{4}-\\d{2}-\\d{10}$";
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Jdbc2 window = new Jdbc2();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Jdbc2() {
		initialize();
	}

	public void clearUsuario() {
		idUsuariotextField.setText("");
		nombretextField.setText("");
		edadtextField.setText("");
		cuentaBancariatextField.setText("");
	}

	public void clearBici() {
		idbicitextField.setText("");
		disponibilidadtextField.setText("");
		valoraciontextField.setText("");
	}

	public boolean usuarioTieneBicicletaAlquilada(int usuarioid) {
		boolean tieneBicicletaAlquilada = false;
		try {
			Connection conn = ConnectionSingleton.getConnection();
			PreparedStatement stmt = conn
					.prepareStatement("SELECT count(*) FROM bici WHERE usuarioid=? and disponibilidad = ?");
			stmt.setInt(1, usuarioid);
			stmt.setInt(2, 0);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				int count = rs.getInt(1);
				if (count > 0) {
					tieneBicicletaAlquilada = true;
				}
			}
			rs.close();
			stmt.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
		return tieneBicicletaAlquilada;
	}

	public boolean biciEstaDisponible(int idbici) {
		boolean biciDisponible = false;
		try {
			Connection con = ConnectionSingleton.getConnection();
			PreparedStatement stmt = con.prepareStatement("SELECT idbici FROM bici WHERE idbici = ? AND disponibilidad = ?");
			stmt.setInt(1, idbici);
			stmt.setInt(2, 1);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				 biciDisponible = true;
			}
			stmt.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
		return biciDisponible;
	}
	
	public boolean hayBicisParaDevolver(Connection con) {
	    try {
	        PreparedStatement countStmt = con.prepareStatement(
	                "SELECT COUNT(*) FROM operacion WHERE alq_dev = 'A'");
	        ResultSet countRs = countStmt.executeQuery();
	        if (countRs.next()) {
	            int count = countRs.getInt(1);
	            return count > 0;
	        }
	        countStmt.close();
	    } catch (SQLException ex) {
	        ex.printStackTrace();
	        JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	    }
	    return false;
	}

	public boolean biciAlquilada(int idbici) {
	    boolean biciAlquilada = false;
	    try {
	        Connection con = ConnectionSingleton.getConnection();
	        PreparedStatement stmt = con.prepareStatement(
	                "SELECT disponibilidad FROM bici WHERE idbici=?");
	        stmt.setInt(1, idbici);
	        ResultSet rs1 = stmt.executeQuery();
	        rs1.next();
	        int disponibilidad = rs1.getInt(1);
	        
	        if (disponibilidad > 0) {
	        	biciAlquilada = false;
	        } else {
	        	biciAlquilada = true;
	        }

	    } catch (SQLException ex) {
	        ex.printStackTrace();
	        JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	    }
	    return biciAlquilada;
	}
	
	public boolean comprobarOperacion(int idoperacion){
		boolean operacionincorrecta = false;
		try {
		Connection con = ConnectionSingleton.getConnection();
		PreparedStatement stmt = con.prepareStatement("SELECT alq_dev FROM operacion WHERE idoperacion = ?");
		stmt.setInt(1, idoperacion);
		ResultSet rs = stmt.executeQuery();
		
		
		if (rs.next()) {
            String indicador = rs.getString("alq_dev");
        if ("D".equals(indicador)) { 
                operacionincorrecta = true;
            }
        } else {
        	String indicador = rs.getString(1);
        
        if (indicador != null && indicador.equals("D")) {
        	operacionincorrecta = true;
        } else {
        	operacionincorrecta = false;
        }
        }
		} catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
    return operacionincorrecta;
}
	
	public boolean comprobarExpReg(String palabra, String er) {
		Pattern pat = Pattern.compile(er);
		Matcher mat = pat.matcher(palabra);
		if (mat.matches()) {
			return true;
		} else {
			return false;
		}
	}

	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame("Alquiler de BICICLETAS");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(900, 600);
		frame.getContentPane().setLayout(null);

		
		// frame.getContentPane().add(mainPanel);

		// TOPPANEL
		// TEXTFIELD
		// USUARIO
		idUsuariotextField = new JTextField();
		idUsuariotextField.setEditable(false);
		idUsuariotextField.setBounds(120, 160, 198, 25);
		frame.getContentPane().add(idUsuariotextField);

		nombretextField = new JTextField();
		nombretextField.setBounds(120, 188, 198, 25);
		frame.getContentPane().add(nombretextField);

		edadtextField = new JTextField();
		edadtextField.setBounds(120, 216, 198, 25);
		frame.getContentPane().add(edadtextField);

		cuentaBancariatextField = new JTextField();
		cuentaBancariatextField.setBounds(120, 244, 198, 25);
		frame.getContentPane().add(cuentaBancariatextField);

		// BICI
		// TEXTFIELD
		idbicitextField = new JTextField();
		idbicitextField.setEditable(false);
		idbicitextField.setBounds(567, 160, 145, 25);
		frame.getContentPane().add(idbicitextField);

		disponibilidadtextField = new JTextField();
		disponibilidadtextField.setEditable(false);
		disponibilidadtextField.setBounds(567, 188, 145, 25);
		frame.getContentPane().add(disponibilidadtextField);

		valoraciontextField = new JTextField();
		valoraciontextField.setBounds(567, 216, 145, 25);
		frame.getContentPane().add(valoraciontextField);

		// OPERACION
		fechaIniciotextField = new JTextField();
		fechaIniciotextField.setEditable(false);
		fechaIniciotextField.setBounds(514, 306, 198, 25);
		frame.getContentPane().add(fechaIniciotextField);
		fechaIniciotextField.setColumns(10);

		fechaFintextField = new JTextField();
		fechaFintextField.setEditable(false);
		fechaFintextField.setBounds(514, 342, 198, 25);
		frame.getContentPane().add(fechaFintextField);
		fechaFintextField.setColumns(10);

		
		textFieldimporte = new JTextField();
		textFieldimporte.setBounds(246, 308, 96, 20);
		frame.getContentPane().add(textFieldimporte);
		textFieldimporte.setColumns(10);
		
		usuarioIdtextField = new JTextField();
		usuarioIdtextField.setBounds(567, 269, 96, 20);
		frame.getContentPane().add(usuarioIdtextField);
		usuarioIdtextField.setColumns(10);
		
		// U S U A R I O TABLE
		DefaultTableModel usuariomodel = new DefaultTableModel();
		usuariomodel.addColumn("ID");
		usuariomodel.addColumn("Nombre");
		usuariomodel.addColumn("Edad");
		usuariomodel.addColumn("Cuenta Bancaria");

		Connection con1;

		try {
			con1 = ConnectionSingleton.getConnection();
			Statement stmt1 = con1.createStatement();
			ResultSet rs1 = stmt1.executeQuery("SELECT * FROM usuario");
			while (rs1.next()) {
				Object[] row = new Object[4];
				row[0] = rs1.getInt("idusuario");
				row[1] = rs1.getString("nombre");
				row[2] = rs1.getInt("edad");
				row[3] = rs1.getString("cuenta_bancaria");
				usuariomodel.addRow(row);
			}
			rs1.close();
			stmt1.close();
			con1.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		usuariotable = new JTable(usuariomodel);
		usuariotable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

		int[] anchosColumnasUsuario = { 30, 120, 40, 200 };
		for (int i = 0; i < anchosColumnasUsuario.length; i++) {
			usuariotable.getColumnModel().getColumn(i).setPreferredWidth(anchosColumnasUsuario[i]);
		}
		JScrollPane usuarioscrollPane = new JScrollPane(usuariotable);
		usuarioscrollPane.setBounds(10, 23, 391, 131);
		usuarioscrollPane.setPreferredSize(new Dimension(200, 200));
		frame.getContentPane().add(usuarioscrollPane);
		// frame.getContentPane().add(usuarioscrollPane);

		usuariotable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int index = usuariotable.getSelectedRow();
				if (index >= 0) {
					TableModel usuariomodel = usuariotable.getModel();

					idUsuariotextField.setText(usuariomodel.getValueAt(index, 0).toString());
					nombretextField.setText(usuariomodel.getValueAt(index, 1).toString());
					edadtextField.setText(usuariomodel.getValueAt(index, 2).toString());
					cuentaBancariatextField.setText(usuariomodel.getValueAt(index, 3).toString());
				}
			}
		});

		// B I C I TABLE
		DefaultTableModel bicimodel = new DefaultTableModel();
		bicimodel.addColumn("ID");
		bicimodel.addColumn("Disponibilidad");
		bicimodel.addColumn("Valoracion");
		bicimodel.addColumn("Fecha inicio");
		bicimodel.addColumn("Fecha Fin");
		bicimodel.addColumn("Importe");
		bicimodel.addColumn("Usuario id");

		Connection con10 = null;
		try {
			con10 = ConnectionSingleton.getConnection();
			Statement stmt10 = con10.createStatement();
			ResultSet rs10 = stmt10.executeQuery("SELECT idbici, disponibilidad, valoracion,fecha_inicio,fecha_fin,importe,usuarioid FROM bici");
			while (rs10.next()) {
				Object[] row = new Object[7];
				row[0] = rs10.getInt("idbici");
				row[1] = rs10.getString("disponibilidad");
				row[2] = rs10.getString("valoracion");
				row[3] = rs10.getString("fecha_inicio");
				row[4] = rs10.getString("fecha_fin");
				row[5] = rs10.getString("importe");
				row[6] = rs10.getString("usuarioid");
				bicimodel.addRow(row);
			}
			rs10.close();
			stmt10.close();
			con10.close();
		} catch (SQLException er) {
			er.printStackTrace();
		}
		bicitable = new JTable(bicimodel);
		bicitable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

		int[] anchosColumnasBici = { 20, 30, 50,120,120,50,30 };
		for (int i = 0; i < anchosColumnasBici.length; i++) {
			bicitable.getColumnModel().getColumn(i).setPreferredWidth(anchosColumnasBici[i]);
		}
		JScrollPane biciscrollPane = new JScrollPane(bicitable);
		biciscrollPane.setBounds(411, 23, 465, 131);
		biciscrollPane.setPreferredSize(new Dimension(200, 200));
		frame.getContentPane().add(biciscrollPane);

		bicitable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int index = bicitable.getSelectedRow();
				if (index >= 0) {
					TableModel bicimodel = bicitable.getModel();

					idbicitextField.setText(
							bicimodel.getValueAt(index, 0) != null ? bicimodel.getValueAt(index, 0).toString() : "");
					disponibilidadtextField.setText(
							bicimodel.getValueAt(index, 1) != null ? bicimodel.getValueAt(index, 1).toString() : "");
					valoraciontextField.setText(
							bicimodel.getValueAt(index, 2) != null ? bicimodel.getValueAt(index, 2).toString() : "");
					fechaIniciotextField.setText(
							bicimodel.getValueAt(index, 3) != null ? bicimodel.getValueAt(index, 3).toString() : "");
					fechaFintextField.setText(
							bicimodel.getValueAt(index, 4) != null ? bicimodel.getValueAt(index, 4).toString() : "");
					textFieldimporte.setText(
							bicimodel.getValueAt(index, 5) != null ? bicimodel.getValueAt(index, 5).toString() : "");

					usuarioidtextField.setText(
							bicimodel.getValueAt(index, 6) != null ? bicimodel.getValueAt(index, 6).toString() : "");

				}
			}
		});

		// USUARIO BOTONES
		// MOSTRAR USUARIO
		JButton mostrarUsuariobtn = new JButton("Mostrar Usuario");
		mostrarUsuariobtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String edadText = edadtextField.getText();

				if (!edadText.isEmpty()) {
					try {
						Connection con4 = null;
						try {
							con4 = ConnectionSingleton.getConnection();
							Statement stmt = con4.createStatement();
							ResultSet rs = stmt.executeQuery("SELECT * FROM usuario");
							usuariomodel.setRowCount(0);
							while (rs.next()) {
								Object[] row = new Object[4];
								row[0] = rs.getInt("idusuario");
								row[1] = rs.getString("nombre");
								row[2] = rs.getInt("edad");
								row[3] = rs.getString("cuenta_bancaria");
								usuariomodel.addRow(row);
							}
							rs.close();
							stmt.close();
							con4.close();
						} catch (SQLException e1) {
							e1.printStackTrace();
						}
					} catch (NumberFormatException ex) {

						System.err.println("El valor ingresado para la edad no es valido.");
					}
				} else {

					 JOptionPane.showMessageDialog(null, "Operacion realizada", "Error", JOptionPane.ERROR_MESSAGE);
				}

			}
		});
		mostrarUsuariobtn.setBounds(328, 216, 125, 25);
		frame.getContentPane().add(mostrarUsuariobtn);

		// CREAR USUARIO
		JButton crearUsuariobtn = new JButton("Crear Usuario");
		crearUsuariobtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String nombre = nombretextField.getText();
				String edadText = edadtextField.getText();
				String cuenta_bancaria = cuentaBancariatextField.getText();

				if (nombre == null || nombre.isEmpty() || nombre.trim().isEmpty()) {
					JOptionPane.showMessageDialog(null, "Inserte nombre");
					nombretextField.requestFocus();
					return;
				}
				if (edadText == null || edadText.isEmpty() || edadText.trim().isEmpty()) {
					JOptionPane.showMessageDialog(null, "Inserte edad");
					edadtextField.requestFocus();
					return;
				} /*
					 * else { int edad = Integer.parseInt(edadText); }
					 */

				if (cuenta_bancaria == null || cuenta_bancaria.isEmpty() || cuenta_bancaria.trim().isEmpty()) {
					JOptionPane.showMessageDialog(null, "Inserte cuenta bancaria");
					cuentaBancariatextField.requestFocus();
					return;
				}
				if (!comprobarExpReg(cuenta_bancaria, CUENTABANCARIA)) {
					JOptionPane.showMessageDialog(null, "Formato de cuenta bancaria incorrecto");
					cuentaBancariatextField.requestFocus();
					return;
				}

				Connection con4 = null;

				int edad = Integer.parseInt(edadText);
				try {
					con4 = ConnectionSingleton.getConnection();
					PreparedStatement ins_pstmt = con4
							.prepareStatement("INSERT INTO usuario (nombre, edad, cuenta_bancaria) VALUES (?, ?, ?)");
					ins_pstmt.setString(1, nombre);
					ins_pstmt.setInt(2, edad);
					ins_pstmt.setString(3, cuenta_bancaria);
					ins_pstmt.executeUpdate();
					JOptionPane.showMessageDialog(null, "Usuario creado");
					mostrarUsuariobtn.doClick();
					clearUsuario();

				} catch (SQLException ex) {
					ex.printStackTrace();

					JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				} catch (NumberFormatException ex) {

					System.err.println("El valor ingresado para la edad no es válido.");
				}
			}

		});
		crearUsuariobtn.setBounds(328, 160, 125, 25);
		frame.getContentPane().add(crearUsuariobtn);

		// ACTUALIZAR USUARIO
		JButton actualizarUsuariobtn = new JButton("Actualizar Usuario");
		actualizarUsuariobtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String idusuario = idUsuariotextField.getText();
				String nombre = nombretextField.getText();
				String edad = edadtextField.getText();
				String cuenta_bancaria = cuentaBancariatextField.getText();

				if (nombre == null || nombre.isEmpty() || nombre.trim().isEmpty()) {
					JOptionPane.showMessageDialog(null, "Inserte nombre");
					nombretextField.requestFocus();
					return;
				}
				if (edad == null || edad.isEmpty() || edad.trim().isEmpty()) {
					JOptionPane.showMessageDialog(null, "Inserte edad");
					edadtextField.requestFocus();
					return;
				}
				if (cuenta_bancaria == null || cuenta_bancaria.isEmpty() || cuenta_bancaria.trim().isEmpty()) {
					JOptionPane.showMessageDialog(null, "Inserte cuenta bancaria");
					cuentaBancariatextField.requestFocus();
					return;
				}
				if (!comprobarExpReg(cuenta_bancaria, CUENTABANCARIA)) {
					JOptionPane.showMessageDialog(null, "Formato de cuenta bancaria incorrecto");
					cuentaBancariatextField.requestFocus();
					return;
				}
				Connection con5 = null;

				try {
					con5 = ConnectionSingleton.getConnection();
					PreparedStatement upd_pstmt = con5.prepareStatement(
							"UPDATE usuario SET nombre = ?, edad = ?, cuenta_bancaria = ?  WHERE idusuario = ?");
					upd_pstmt.setString(1, nombre);
					upd_pstmt.setInt(2, Integer.parseInt(edad));
					upd_pstmt.setString(3, cuenta_bancaria);
					upd_pstmt.setInt(4, Integer.parseInt(idusuario));
					upd_pstmt.executeUpdate();
					JOptionPane.showMessageDialog(null, "Usuario actualizado");
					mostrarUsuariobtn.doClick();
					clearUsuario();

				} catch (SQLException ex) {
					ex.printStackTrace();

					JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				} catch (NumberFormatException ex) {

					System.err.println("El valor ingresado para la edad no es válido.");
				}
			}
		});
		actualizarUsuariobtn.setBounds(328, 188, 125, 25);
		frame.getContentPane().add(actualizarUsuariobtn);

		// BORRAR USUARIO
		JButton borrarUsuariobtn = new JButton("Borrar Usuario");
		borrarUsuariobtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String idusuarioText = idUsuariotextField.getText();
				 if (idusuarioText.isEmpty()) {
			         JOptionPane.showMessageDialog(null, "Por favor, seleccione un usuario.", "Error", JOptionPane.ERROR_MESSAGE);
			            return; 
			    }
				int idusuario = Integer.parseInt(idusuarioText);

				if (usuarioTieneBicicletaAlquilada(idusuario)) {
					JOptionPane.showMessageDialog(null,
							"No se puede borrar el usuario porque tiene una bici alquilada.");
					clearUsuario();
					return;
				}

				Connection conBorrarUsuario = null;
				try {
					conBorrarUsuario = ConnectionSingleton.getConnection();
					PreparedStatement dele_pstmt = conBorrarUsuario
							.prepareStatement("DELETE FROM usuario WHERE idusuario = ?");
					dele_pstmt.setInt(1, idusuario);
					int rowsDeleted = dele_pstmt.executeUpdate();
					if (rowsDeleted >= 0) {
						mostrarUsuariobtn.doClick();
						JOptionPane.showMessageDialog(null, "Usuario borrado");
						clearUsuario();
						dele_pstmt.close();
					}
				} catch (SQLException ex) {
					ex.printStackTrace();
					JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		borrarUsuariobtn.setBounds(328, 244, 125, 25);
		frame.getContentPane().add(borrarUsuariobtn);

		// BICI
		// MOSTRAR BICI
		mostrarBicibtn = new JButton("Mostrar Bici");
		mostrarBicibtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Connection con6 = null;
					try {
						con6 = ConnectionSingleton.getConnection();
						Statement stmt = con6.createStatement();
						ResultSet rs = stmt.executeQuery("SELECT * FROM bici");
						bicimodel.setRowCount(0);
						while (rs.next()) {
							Object[] row = new Object[7];
							row[0] = rs.getInt("idbici");
							row[1] = rs.getInt("disponibilidad");
							row[2] = rs.getString("valoracion");
							row[3] = rs.getString("fecha_inicio");
							row[4] = rs.getString("fecha_fin");
							row[5] = rs.getDouble("importe");
							row[6] = rs.getInt("usuarioid");
	
							bicimodel.addRow(row);
						}
						rs.close();
						stmt.close();
						con6.close();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				} catch (NumberFormatException ex) {

					System.err.println("El valor ingresado no es válido.");
				}
			}
		});
		mostrarBicibtn.setBounds(722, 216, 110, 25);
		frame.getContentPane().add(mostrarBicibtn);

		crearBicibtn = new JButton("Crear Bici");
		crearBicibtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int disponibilidad = 1;
				String valoracion = "*****";

				Connection con6 = null;
				try {
					con6 = ConnectionSingleton.getConnection();
					PreparedStatement ins_pstmt = con6
							.prepareStatement("INSERT INTO bici (disponibilidad, valoracion) VALUES (?, ?)");
					ins_pstmt.setInt(1, disponibilidad);
					ins_pstmt.setString(2, valoracion);
					int rowsInserted = ins_pstmt.executeUpdate();
					if (rowsInserted > 0) {
						JOptionPane.showMessageDialog(null, "Bici creada con exito");
						mostrarBicibtn.doClick();
					}
					ins_pstmt.close();

					clearBici();
				} catch (SQLException ex) {
					ex.printStackTrace();
					JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}

			}
		});
		crearBicibtn.setBounds(722, 160, 110, 25);
		frame.getContentPane().add(crearBicibtn);

		actualizarBicibtn = new JButton("Actualizar Bici");
		actualizarBicibtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String idbici = idbicitextField.getText();
				String disponibilidad = disponibilidadtextField.getText();
				String valoracion = valoraciontextField.getText();

				Connection conactualizaBici = null;

				try {
					conactualizaBici = ConnectionSingleton.getConnection();
					PreparedStatement upd_pstmt = conactualizaBici
							.prepareStatement("UPDATE bici SET disponibilidad = ?, valoracion = ?  WHERE idbici = ?");
					upd_pstmt.setInt(1, Integer.parseInt(disponibilidad));
					upd_pstmt.setString(2, valoracion);
					upd_pstmt.setInt(3, Integer.parseInt(idbici));
					upd_pstmt.executeUpdate();
					JOptionPane.showMessageDialog(null, "Bici actualizada");
					mostrarUsuariobtn.doClick();
					clearUsuario();

				} catch (SQLException ex) {
					ex.printStackTrace();

					JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				} catch (NumberFormatException ex) {

					 JOptionPane.showMessageDialog(null, "Seleccione una bicicleta.", "Error", JOptionPane.ERROR_MESSAGE);
				}

			}
		});
		actualizarBicibtn.setBounds(722, 188, 110, 25);
		frame.getContentPane().add(actualizarBicibtn);

		// BORRAR BICI
		borrarBicibtn = new JButton("Borrar Bici");
		borrarBicibtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String idbiciText = idbicitextField.getText();	
				if (idbiciText.isEmpty()) {
			            JOptionPane.showMessageDialog(null, "Por favor, seleccione una bicicleta.", "Error", JOptionPane.ERROR_MESSAGE);
			            return; // 
			        }
				int idbici = Integer.parseInt(idbiciText);
				if (!biciEstaDisponible(idbici)) {
			            JOptionPane.showMessageDialog(null,
			                    "No se puede borrar una bici alquilada.");
			            clearBici();
			            return;
			        }
				
				Connection con = null;
				
				try {
					
					con = ConnectionSingleton.getConnection();
					PreparedStatement dele_pstmt = con.prepareStatement("DELETE FROM bici WHERE idbici = ?");
					dele_pstmt.setInt(1, idbici);
					dele_pstmt.executeUpdate();
					JOptionPane.showMessageDialog(null, "Bici borrada");
					mostrarBicibtn.doClick();
					clearBici();
					dele_pstmt.close();
				} catch (SQLException ex) {
					ex.printStackTrace();
					JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}

			}
		});
		borrarBicibtn.setBounds(722, 244, 110, 25);
		frame.getContentPane().add(borrarBicibtn);

		// ALQUILAR BICI
		alquilarBicibtn = new JButton("Alquilar Bici");
		alquilarBicibtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
	
				int selectedBiciRowIndex = bicitable.getSelectedRow();
				int selectedUsuarioRowIndex = usuariotable.getSelectedRow();

				
				if (selectedBiciRowIndex >= 0 && selectedUsuarioRowIndex>=0) {
					int idusuario = (int) usuariotable.getModel().getValueAt(selectedUsuarioRowIndex, 0);
					int biciid = (int) bicitable.getModel().getValueAt(selectedBiciRowIndex, 0);
	

					if (usuarioTieneBicicletaAlquilada(idusuario)) {
						JOptionPane.showMessageDialog(null,
								"No se puede alquilar porque el usuario ya tiene una bicicleta alquilada.");
						clearUsuario();
						clearBici();
						return;
					} else if (!biciEstaDisponible(biciid)) {
						JOptionPane.showMessageDialog(null, "La bicicleta seleccionada ya esta alquilada.", "Error",
								JOptionPane.ERROR_MESSAGE);
						clearUsuario();
						clearBici();
						return;
					}
					
					
					LocalDateTime fecha_inicio_now = LocalDateTime.now();
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
					String fecha_inicio = fecha_inicio_now.format(formatter);

					try {
						Connection con = ConnectionSingleton.getConnection();
						PreparedStatement updateStmt = con
								.prepareStatement("UPDATE bici SET disponibilidad=?,usuarioid=?,fecha_inicio=?,fecha_fin=?,importe=? WHERE idbici=?");
						updateStmt.setInt(1, 0);
						updateStmt.setInt(2, idusuario);
						updateStmt.setString(3, fecha_inicio);
						updateStmt.setString(4, " ");
						updateStmt.setDouble(5, 0.0);
						updateStmt.setInt(6, biciid);
						int rowsUpdated = updateStmt.executeUpdate();
							if (rowsUpdated > 0) {
							
								JOptionPane.showMessageDialog(null, "Bici alquilada con exito");
								mostrarBicibtn.doClick();
							
								usuariotable.clearSelection();
								clearUsuario();
							}
						
						updateStmt.close();

						clearBici();
						mostrarBicibtn.doClick();

					} catch (SQLException ex) {
						ex.printStackTrace();
						JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					}
			
				} else {
					JOptionPane.showMessageDialog(null, "Seleccione un usuario y una bici.", "Error",
							JOptionPane.ERROR_MESSAGE);

				}

			}
		});
		alquilarBicibtn.setBounds(203, 390, 110, 62);
		frame.getContentPane().add(alquilarBicibtn);

		// DEVOLVER BICI
		devolverBicibtn = new JButton("Devolver Bici");
		devolverBicibtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int selectedBiciRowIndex = bicitable.getSelectedRow();	
//				int idbici = bicitable.getSelectedRow();
//				int usuarioid = bicitable.getSelectedRow();
				
				if (selectedBiciRowIndex >= 0) {
					int idbici = (int) bicitable.getModel().getValueAt(selectedBiciRowIndex, 0);
					int usuarioid = Integer.parseInt((String) bicitable.getModel().getValueAt(selectedBiciRowIndex, 6));

					if (!usuarioTieneBicicletaAlquilada(usuarioid)) {
						JOptionPane.showMessageDialog(null,
								"No se puede devolver porque la bicicleta no esta alquilada.");
						clearUsuario();
						clearBici();
						return;
					} else if (biciEstaDisponible(idbici)) {
						JOptionPane.showMessageDialog(null, "La bicicleta seleccionada no esta alquilada.", "Error",
								JOptionPane.ERROR_MESSAGE);
						clearUsuario();
						clearBici();
						return;
					}
					
					
					// FECHA
					LocalDateTime fecha_fin_now = LocalDateTime.now();
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

					String fecha_fin = fecha_fin_now.format(formatter);
					int selectedRowIndex = 0;
					String fecha_inicio_str = bicimodel.getValueAt(selectedRowIndex, 3).toString();

					LocalDateTime fecha_inicio = LocalDateTime.parse(fecha_inicio_str,
							DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));

					long segundosTranscurridos = ChronoUnit.SECONDS.between(fecha_inicio, fecha_fin_now);
					double importe = segundosTranscurridos * 0.02;

					try {
						Connection con = ConnectionSingleton.getConnection();
						PreparedStatement updateStmt = con
								.prepareStatement("UPDATE bici SET disponibilidad=?,fecha_fin=?,importe=? WHERE idbici=?");
						updateStmt.setInt(1, 1);
						updateStmt.setString(2, fecha_fin);
						updateStmt.setDouble(3, importe);
//						updateStmt.setInt(4, usuarioid);
						updateStmt.setInt(4, idbici);
						int rowsUpdated = updateStmt.executeUpdate();
							if (rowsUpdated > 0) {
							
								JOptionPane.showMessageDialog(null, "Bici devuelta con exito");
								mostrarBicibtn.doClick();
								
								usuariotable.clearSelection();
								clearUsuario();
							}
						
						updateStmt.close();

						clearBici();
						mostrarBicibtn.doClick();
						

					} catch (SQLException ex) {
						ex.printStackTrace();
						JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					}
			
				} else {
					JOptionPane.showMessageDialog(null, "Seleccione un usuario y una bici.", "Error",
							JOptionPane.ERROR_MESSAGE);

				}


				}
		});
		devolverBicibtn.setBounds(469, 390, 110, 62);
		frame.getContentPane().add(devolverBicibtn);

		// LABELS
		lblNewLabel = new JLabel("USUARIOS");
		lblNewLabel.setFont(new Font("Verdana", Font.BOLD, 22));
		lblNewLabel.setBounds(160, 0, 153, 25);
		frame.getContentPane().add(lblNewLabel);

		lblNewLabel_1 = new JLabel("Codigo Usuario:");
		lblNewLabel_1.setBounds(10, 160, 88, 14);
		frame.getContentPane().add(lblNewLabel_1);

		lblNewLabel_2 = new JLabel("Nombre:");
		lblNewLabel_2.setBounds(10, 188, 88, 14);
		frame.getContentPane().add(lblNewLabel_2);

		lblNewLabel_3 = new JLabel("Edad:");
		lblNewLabel_3.setBounds(10, 216, 88, 14);
		frame.getContentPane().add(lblNewLabel_3);

		lblNewLabel_4 = new JLabel("Cuenta Bancaria:");
		lblNewLabel_4.setBounds(10, 249, 95, 14);
		frame.getContentPane().add(lblNewLabel_4);

		lblNewLabel_5 = new JLabel("BICIS");
		lblNewLabel_5.setFont(new Font("Verdana", Font.BOLD, 22));
		lblNewLabel_5.setBounds(533, 0, 153, 25);
		frame.getContentPane().add(lblNewLabel_5);

		lblNewLabel_6 = new JLabel("Codigo Bici:");
		lblNewLabel_6.setBounds(483, 168, 80, 14);
		frame.getContentPane().add(lblNewLabel_6);

		lblNewLabel_7 = new JLabel("Disponibilidad:");
		lblNewLabel_7.setBounds(483, 193, 80, 14);
		frame.getContentPane().add(lblNewLabel_7);

		lblNewLabel_8 = new JLabel("Valoracion:");
		lblNewLabel_8.setBounds(483, 218, 80, 14);
		frame.getContentPane().add(lblNewLabel_8);

		lblNewLabel_14 = new JLabel("0: NO DISPONIBLE // 1: DISPONIBLE");
		lblNewLabel_14.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblNewLabel_14.setBounds(483, 244, 229, 14);
		frame.getContentPane().add(lblNewLabel_14);

		JLabel lblNewLabel_9 = new JLabel("Fecha INICIO:");
		lblNewLabel_9.setBounds(424, 303, 80, 14);
		frame.getContentPane().add(lblNewLabel_9);

		JLabel lblNewLabel_10 = new JLabel("Fecha FIN:");
		lblNewLabel_10.setBounds(424, 347, 80, 14);
		frame.getContentPane().add(lblNewLabel_10);



		lblNewLabel_15 = new JLabel("");
		lblNewLabel_15.setBounds(612, 11, 229, 131);
		frame.getContentPane().add(lblNewLabel_15);
		

		
		JLabel lblNewLabel_16 = new JLabel("Usuario ID");
		lblNewLabel_16.setBounds(469, 269, 49, 14);
		frame.getContentPane().add(lblNewLabel_16);
	
		

		frame.setVisible(true);

	}
}
