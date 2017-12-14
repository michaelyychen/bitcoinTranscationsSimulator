package edu.nyu.crypto.csci3033.transactions;

import org.bitcoinj.core.*;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.TransactionSignature;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;

import java.io.File;
import java.math.BigInteger;

import static org.bitcoinj.script.ScriptOpCodes.*;
import static org.bitcoinj.script.ScriptOpCodes.OP_VERIFY;

/**
 * Created by bbuenz on 24.09.15.
 */
public class PayToPubKeyHash extends ScriptTransaction {
    // TODO: Problem 1
    private ECKey key;

    public PayToPubKeyHash(NetworkParameters parameters, File file, String password) {

        super(parameters, file, password);
        key = getWallet().freshReceiveKey();


    }

    // for external vanity address
    public PayToPubKeyHash(NetworkParameters parameters, File file, String password, String priv) {

        super(parameters, file, password);
        try {
            key = new DumpedPrivateKey(parameters, priv).getKey();
        } catch (AddressFormatException e) {
            e.printStackTrace();
        }

    }


    @Override
    public Script createInputScript() {

        ScriptBuilder builder = new ScriptBuilder();

        builder.op(OP_DUP);
        builder.op(OP_HASH160);
        builder.data(key.getPubKeyHash());
        builder.op(OP_EQUALVERIFY);
        builder.op(OP_CHECKSIG);

        return builder.build();

    }

    @Override
    public Script createRedemptionScript(Transaction unsignedTransaction) {
        TransactionSignature txSig = sign(unsignedTransaction, key);

        ScriptBuilder builder = new ScriptBuilder();
        builder.data(txSig.encodeToBitcoin());
        builder.data(key.getPubKey());
        return builder.build();
    }
}
