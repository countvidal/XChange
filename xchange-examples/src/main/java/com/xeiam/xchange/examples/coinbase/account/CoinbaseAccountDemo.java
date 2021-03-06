/**
 * Copyright (C) 2012 - 2014 Xeiam LLC http://xeiam.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.xeiam.xchange.examples.coinbase.account;

import java.io.IOException;
import java.util.List;

import com.xeiam.xchange.Exchange;
import com.xeiam.xchange.coinbase.dto.CoinbaseBaseResponse;
import com.xeiam.xchange.coinbase.dto.account.CoinbaseAccountChanges;
import com.xeiam.xchange.coinbase.dto.account.CoinbaseAddress;
import com.xeiam.xchange.coinbase.dto.account.CoinbaseAddresses;
import com.xeiam.xchange.coinbase.dto.account.CoinbaseContacts;
import com.xeiam.xchange.coinbase.dto.account.CoinbaseRecurringPayment;
import com.xeiam.xchange.coinbase.dto.account.CoinbaseRecurringPayments;
import com.xeiam.xchange.coinbase.dto.account.CoinbaseToken;
import com.xeiam.xchange.coinbase.dto.account.CoinbaseTransaction;
import com.xeiam.xchange.coinbase.dto.account.CoinbaseTransaction.CoinbaseRequestMoneyRequest;
import com.xeiam.xchange.coinbase.dto.account.CoinbaseTransactions;
import com.xeiam.xchange.coinbase.dto.account.CoinbaseUser;
import com.xeiam.xchange.coinbase.dto.account.CoinbaseUsers;
import com.xeiam.xchange.coinbase.dto.marketdata.CoinbaseMoney;
import com.xeiam.xchange.coinbase.service.polling.CoinbaseAccountService;
import com.xeiam.xchange.currency.MoneyUtils;
import com.xeiam.xchange.dto.account.AccountInfo;
import com.xeiam.xchange.examples.coinbase.CoinbaseDemoUtils;
import com.xeiam.xchange.service.polling.PollingAccountService;

/**
 * @author jamespedwards42
 */
public class CoinbaseAccountDemo {

  public static void main(String[] args) throws IOException {

    Exchange coinbase = CoinbaseDemoUtils.createExchange();
    PollingAccountService accountService = coinbase.getPollingAccountService();

    generic(accountService);
    raw((CoinbaseAccountService) accountService);
  }

  private static void generic(PollingAccountService accountService) throws IOException {

    AccountInfo accountInfo = accountService.getAccountInfo();
    System.out.println("Account Info: " + accountInfo);

    String depositAddress = accountService.requestBitcoinDepositAddress();
    System.out.println("Deposit Address: " + depositAddress);

    // String transactionHash = accountService.withdrawFunds(new BigDecimal(".01"), "1CYmvfR53AYPj87TjxXZQrLZ8z8dRUKDMs");
    // System.out.println("Bitcoin blockchain transaction hash: " + transactionHash);
  }

  public static void raw(CoinbaseAccountService accountService) throws IOException {

    CoinbaseMoney balance = accountService.getCoinbaseBalance();
    System.out.println(balance);

    demoUsers(accountService);

    demoAddresses(accountService);

    demoTransactions(accountService);

    CoinbaseAccountChanges accountChanges = accountService.getCoinbaseAccountChanges();
    System.out.println(accountChanges);

    CoinbaseContacts contacts = accountService.getCoinbaseContacts();
    System.out.println(contacts);

    demoTokens(accountService);

    demoRecurringPayments(accountService);
  }

  private static void demoRecurringPayments(CoinbaseAccountService accountService) throws IOException {

    CoinbaseRecurringPayments recurringPayments = accountService.getCoinbaseRecurringPayments();
    System.out.println(recurringPayments);

    List<CoinbaseRecurringPayment> recurringPaymentsList = recurringPayments.getRecurringPayments();
    if (!recurringPaymentsList.isEmpty()) {
      CoinbaseRecurringPayment recurringPayment = recurringPaymentsList.get(0);
      recurringPayment = accountService.getCoinbaseRecurringPayment(recurringPayment.getId());
      System.out.println(recurringPayment);
    }
  }

  private static void demoUsers(CoinbaseAccountService accountService) throws IOException {

    CoinbaseUsers users = accountService.getCoinbaseUsers();
    System.out.println("Current User: " + users);

    CoinbaseUser user = users.getUsers().get(0);
    user.updateTimeZone("Tijuana").updateNativeCurrency("MXN");
    user = accountService.updateCoinbaseUser(user);
    System.out.println("Updated User: " + user);

    CoinbaseUser newUser = CoinbaseUser.createCoinbaseNewUserWithReferrerId("demo@demo.com", "pass1234", "527d2a1ffedcb8b73b000028");
    String oauthClientId = ""; // optional
    CoinbaseUser createdUser = accountService.createCoinbaseUser(newUser, oauthClientId);
    System.out.println("Newly created user: " + createdUser);
  }

  private static void demoTokens(CoinbaseAccountService accountService) throws IOException {

    CoinbaseToken token = accountService.createCoinbaseToken();
    System.out.println(token);

    boolean isAccepted = accountService.redeemCoinbaseToken(token.getTokenId());
    System.out.println(isAccepted);
  }

  private static void demoAddresses(CoinbaseAccountService accountService) throws IOException {

    CoinbaseAddress receiveAddress = accountService.getCoinbaseReceiveAddress();
    System.out.println(receiveAddress);

    CoinbaseAddress generatedReceiveAddress = accountService.generateCoinbaseReceiveAddress("http://www.example.com/callback", "test");
    System.out.println(generatedReceiveAddress);

    CoinbaseAddresses addresses = accountService.getCoinbaseAddresses();
    System.out.println(addresses);
  }

  private static void demoTransactions(CoinbaseAccountService accountService) throws IOException {

    CoinbaseRequestMoneyRequest moneyRequest = CoinbaseTransaction.createMoneyRequest("xchange@demo.com", MoneyUtils.parse("BTC .001")).withNotes("test");
    CoinbaseTransaction pendingTransaction = accountService.requestMoneyCoinbaseRequest(moneyRequest);
    System.out.println(pendingTransaction);

    CoinbaseBaseResponse resendResponse = accountService.resendCoinbaseRequest(pendingTransaction.getId());
    System.out.println(resendResponse);

    CoinbaseBaseResponse cancelResponse = accountService.cancelCoinbaseRequest(pendingTransaction.getId());
    System.out.println(cancelResponse);

    // CoinbaseSendMoneyRequest sendMoneyRequest = CoinbaseTransaction
    // .createSendMoneyRequest("1Fpx2Q6J8TX3PZffgEBTpWSHG37FQBgqKB", MoneyUtils.parse("BTC .01"))
    // .withNotes("Demo Money!").withInstantBuy(false).withUserFee("0.0");
    // CoinbaseTransaction sendTransaction = accountService.sendMoney(sendMoneyRequest);
    // System.out.println(sendTransaction);

    // CoinbaseTransaction completedTransaction = accountService.completeRequest("530010d62b342891e2000083");
    // System.out.println(completedTransaction);

    CoinbaseTransactions transactions = accountService.getCoinbaseTransactions();
    System.out.println(transactions);

    if (transactions.getTotalCount() > 0) {
      CoinbaseTransaction transaction = accountService.getCoinbaseTransaction(transactions.getTransactions().get(0).getId());
      System.out.println(transaction);
    }
  }
}
