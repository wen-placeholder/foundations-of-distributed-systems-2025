package ch.unibas.dmi.dbis.fds._2pc;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.transaction.xa.XAException;
import javax.transaction.xa.Xid;


/**
 * Check the XA stuff here --> https://docs.oracle.com/cd/B14117_01/java.101/b10979/xadistra.htm
 *
 * @author Alexander Stiemer (alexander.stiemer at unibas.ch)
 */
public class OracleXaBank extends AbstractOracleXaBank {


    public OracleXaBank( final String BIC, final String jdbcConnectionString, final String dbmsUsername, final String dbmsPassword ) throws SQLException {
        super( BIC, jdbcConnectionString, dbmsUsername, dbmsPassword );
    }


    @Override
    public float getBalance( final String iban ) throws SQLException {
        // Query the account balance from the database
        String query = "SELECT Balance FROM account WHERE IBAN = ?";

        try ( Connection conn = getXaConnection().getConnection();
              PreparedStatement stmt = conn.prepareStatement( query ) ) {

            stmt.setString( 1, iban );

            try ( ResultSet rs = stmt.executeQuery() ) {
                if ( rs.next() ) {
                    return rs.getFloat( "Balance" );
                } else {
                    throw new SQLException( "Account with IBAN " + iban + " not found" );
                }
            }
        }
    }


    @Override
    public void transfer( final AbstractOracleXaBank TO_BANK, final String ibanFrom, final String ibanTo, final float value ) {
        Xid fromBankXid = null;
        Xid toBankXid = null;

        try {
            // Create a global transaction ID that both branches will share
            Xid globalXid = this.getXid();

            // Start transaction branches on both banks with the same global transaction ID
            fromBankXid = this.startTransaction( globalXid );
            toBankXid = TO_BANK.startTransaction( globalXid );

            // Get connections for both banks
            Connection fromConn = this.getXaConnection().getConnection();
            Connection toConn = TO_BANK.getXaConnection().getConnection();

            // Execute withdraw operation on source bank
            String withdrawSQL = "UPDATE account SET Balance = Balance - ? WHERE IBAN = ?";
            try ( PreparedStatement withdrawStmt = fromConn.prepareStatement( withdrawSQL ) ) {
                withdrawStmt.setFloat( 1, value );
                withdrawStmt.setString( 2, ibanFrom );
                int rowsAffected = withdrawStmt.executeUpdate();

                if ( rowsAffected == 0 ) {
                    throw new SQLException( "Source account " + ibanFrom + " not found or insufficient balance" );
                }
            }

            // Execute deposit operation on target bank
            String depositSQL = "UPDATE account SET Balance = Balance + ? WHERE IBAN = ?";
            try ( PreparedStatement depositStmt = toConn.prepareStatement( depositSQL ) ) {
                depositStmt.setFloat( 1, value );
                depositStmt.setString( 2, ibanTo );
                int rowsAffected = depositStmt.executeUpdate();

                if ( rowsAffected == 0 ) {
                    throw new SQLException( "Target account " + ibanTo + " not found" );
                }
            }

            // End and commit both transaction branches
            this.endTransaction( fromBankXid, false );
            TO_BANK.endTransaction( toBankXid, false );

        } catch ( Exception e ) {
            // If any error occurs, rollback both transaction branches
            try {
                if ( fromBankXid != null ) {
                    this.endTransaction( fromBankXid, true );
                }
            } catch ( XAException rollbackEx ) {
                LOG.log( java.util.logging.Level.SEVERE, "Failed to rollback source bank transaction", rollbackEx );
            }

            try {
                if ( toBankXid != null ) {
                    TO_BANK.endTransaction( toBankXid, true );
                }
            } catch ( XAException rollbackEx ) {
                LOG.log( java.util.logging.Level.SEVERE, "Failed to rollback target bank transaction", rollbackEx );
            }

            // Re-throw the exception as RuntimeException
            throw new RuntimeException( "Transfer failed: " + e.getMessage(), e );
        }
    }
}
