package com.diynova;

import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

public class Main {

    private final static String rpcUrl = "replace this url.";
    public static void main(String[] args) throws IOException, CipherException {
	// write your code here
        System.out.println("Hello, World!");
        Web3j web3 = Web3j.build(new HttpService(rpcUrl));
        NetVersion netVersion = null;
        try {
            netVersion = web3.netVersion().send();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String clientVersion = netVersion.getNetVersion();
        System.out.println(clientVersion);
        /*
        String fileName = WalletUtils.generateNewWalletFile(
                    "123qwe",
                    new File("C:\\dev\\ethereum\\wallet\\"));
        */
        Credentials credentials = null;
        credentials = WalletUtils.loadCredentials(
                    "123qwe",
                    "C:\\dev\\ethereum\\wallet\\UTC--2018-08-06T06-22-19.13000000Z--57a16a05e0efa2eea93d553db52e974b5f92078a.json");

        String fromAddress = credentials.getAddress();
        System.out.println(fromAddress);

        EthGetBalance balance = web3.ethGetBalance(fromAddress,DefaultBlockParameterName.LATEST).send();
        BigInteger b =  balance.getBalance();
        System.out.println(b);

        // get the next available nonce
        EthGetTransactionCount ethGetTransactionCount = web3.ethGetTransactionCount(
                fromAddress, DefaultBlockParameterName.LATEST).send();
        BigInteger nonce = ethGetTransactionCount.getTransactionCount();
        System.out.println(nonce);
        String toAddress = "0x46d00A386E63B686a8d26407E6d6E0CB7c6256fA";

        EthGasPrice ethGasPrice = web3.ethGasPrice().send();
        BigInteger gasPrice = ethGasPrice.getGasPrice();
        System.out.println(gasPrice);

        Transaction tx = Transaction.createEtherTransaction(fromAddress,BigInteger.ONE,BigInteger.ONE,BigInteger.ONE,toAddress,BigInteger.ONE);
        EthEstimateGas ethEstimateGas = web3.ethEstimateGas(tx).send();
        BigInteger gasLimit = ethEstimateGas.getAmountUsed();
        System.out.println(gasLimit);

        // create our transaction
        RawTransaction rawTransaction  = RawTransaction.createEtherTransaction(
                nonce, gasPrice, gasLimit, toAddress, Convert.toWei(BigDecimal.valueOf(10), Convert.Unit.ETHER).toBigInteger());

        // sign & send our transaction
        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, Integer.parseInt(clientVersion),credentials);
        String hexValue = Numeric.toHexString(signedMessage);
        EthSendTransaction ethSendTransaction = web3.ethSendRawTransaction(hexValue).send();
        String hash = ethSendTransaction.getTransactionHash();
        System.out.println(hash);

    }
}
