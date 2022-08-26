package dk.alexandra.fresco.tools.ot.base;

import dk.alexandra.fresco.framework.MaliciousException;
import dk.alexandra.fresco.framework.network.Network;
import dk.alexandra.fresco.framework.util.*;
import dk.alexandra.fresco.tools.ot.otextension.PseudoOtp;

import java.math.BigInteger;
import java.security.MessageDigest;

public abstract class AbstractNaorPinkasOT implements Ot {

  private static final String HASH_ALGORITHM = "SHA-256";
  private final int otherId;
  private final Network network;
  protected final Drng randNum;

  private final MessageDigest hashDigest;

  /**
   * @return a randomly generated NaorPinkas Element
   */
  abstract InterfaceNaorPinkasElement generateRandomNaorPinkasElement();

  /**
   * Decodes an encoded element
   * @param bytes the encoded element represented in bytes
   * @return the decoded element
   */
  abstract InterfaceNaorPinkasElement decodeElement(byte[] bytes);

  abstract BigInteger getDhModulus();

  abstract InterfaceNaorPinkasElement getDhGenerator();


  public AbstractNaorPinkasOT(int otherId, Drbg randBit, Network network) {
    this.otherId = otherId;
    this.network = network;
    this.hashDigest = ExceptionConverter.safe(() -> MessageDigest.getInstance(HASH_ALGORITHM),
        "Missing secure, hash function which is dependent in this library");
    this.randNum = new DrngImpl(randBit);
  }

  @Override
  public void send(StrictBitVector messageZero, StrictBitVector messageOne) {
    int maxBitLength = Math.max(messageZero.getSize(), messageOne.getSize());
    Pair<byte[], byte[]> seedMessages = sendRandomOt();
    byte[] encryptedZeroMessage = PseudoOtp.encrypt(messageZero.toByteArray(),
        seedMessages.getFirst(), maxBitLength / Byte.SIZE);
    byte[] encryptedOneMessage = PseudoOtp.encrypt(messageOne.toByteArray(),
        seedMessages.getSecond(), maxBitLength / Byte.SIZE);
    network.send(otherId, encryptedZeroMessage);
    network.send(otherId, encryptedOneMessage);
  }

  @Override
  public StrictBitVector receive(boolean choiceBit) {
    byte[] seed = receiveRandomOt(choiceBit);
    byte[] encryptedZeroMessage = network.receive(otherId);
    byte[] encryptedOneMessage = network.receive(otherId);
    return recoverTrueMessage(encryptedZeroMessage, encryptedOneMessage, seed, choiceBit);
  }

  /**
   * Completes the sender's part of the Naor-Pinkas OT in order to send two random messages of the
   * length of hash digest.
   *
   * @return The two random messages sent by the sender.
   */
  private Pair<byte[], byte[]> sendRandomOt() {
    InterfaceNaorPinkasElement randPoint = this.generateRandomNaorPinkasElement();
    network.send(otherId, randPoint.toByteArray());
    byte[] tmp = network.receive(otherId);
    InterfaceNaorPinkasElement publicKeyZero = this.decodeElement(tmp);
    InterfaceNaorPinkasElement publicKeyOne = publicKeyZero.inverse().groupOp(randPoint);
    Pair<InterfaceNaorPinkasElement, byte[]> zeroChoiceData = encryptRandomMessage(publicKeyZero);
    Pair<InterfaceNaorPinkasElement, byte[]> oneChoiceData = encryptRandomMessage(publicKeyOne);
    network.send(otherId, zeroChoiceData.getFirst().toByteArray());
    network.send(otherId, oneChoiceData.getFirst().toByteArray());
    return new Pair<>(zeroChoiceData.getSecond(), oneChoiceData.getSecond());
  }


  /**
   * Completes the receiver's part of the Naor-Pinkas OT in order to receive a random message of the
   * length of hash digest.
   *
   * @return The random message received
   */
  private byte[] receiveRandomOt(boolean choiceBit) {
    InterfaceNaorPinkasElement randPoint = this.decodeElement(network.receive(otherId));
    BigInteger privateKey = randNum.nextBigInteger(getDhModulus());
    InterfaceNaorPinkasElement publicKeySigma = getDhGenerator().exponentiation(privateKey);

    InterfaceNaorPinkasElement publicKeyNotSigma = publicKeySigma.inverse().groupOp(randPoint);

    if (choiceBit == false) {
      network.send(otherId, publicKeySigma.toByteArray());
    } else {
      network.send(otherId, publicKeyNotSigma.toByteArray());
    }
    InterfaceNaorPinkasElement encZero = decodeElement(network.receive(otherId));
    InterfaceNaorPinkasElement encOne = decodeElement(network.receive(otherId));
    byte[] message;
    if (choiceBit == false) {
      message = decryptRandomMessage(encZero, privateKey);
    } else {
      message = decryptRandomMessage(encOne, privateKey);
    }
    return message;
  }

  /**
   * Completes the internal Naor-Pinkas encryption.
   * <p>
   * Given a "public key" as input this method constructs an encryption of a random message. Both
   * the encryption and random message are returned.
   * </p>
   *
   * @param publicKey The public key to encrypt with
   * @return A pair where the first element is the ciphertext and the second element is the
   * plaintext.
   */
  private Pair<InterfaceNaorPinkasElement, byte[]> encryptRandomMessage(
      InterfaceNaorPinkasElement publicKey) {

    BigInteger r = randNum.nextBigInteger(getDhModulus());
    InterfaceNaorPinkasElement cipherText = getDhGenerator().exponentiation(r);
    InterfaceNaorPinkasElement toHash = publicKey.exponentiation(r);
    byte[] message = hashDigest.digest(toHash.toByteArray());
    return new Pair<>(cipherText, message);
  }

  /**
   * Completes the internal Naor-Pinkas decryption.
   *
   * @param cipher     The ciphertext to decrypt
   * @param privateKey The private key to use for decryption
   * @return The plain message
   */
  private byte[] decryptRandomMessage(InterfaceNaorPinkasElement cipher, BigInteger privateKey) {
    InterfaceNaorPinkasElement toHash = cipher.exponentiation(privateKey);
    return hashDigest.digest(toHash.toByteArray());
  }

  /**
   * Receive one-time padded OT messages and remove the pad of the one of the messages chosen in the
   * OT.
   *
   * @param encryptedZeroMessage The one-time padded zero-message
   * @param encryptedOneMessage  the one-time padded one-message
   * @param seed                 The seed used for padding of one of the messages
   * @param choiceBit            A bit indicating which message the seed matches. False implies
   *                             message zero and true message one.
   * @return The unpadded message as a StrictBitVector
   */
  private StrictBitVector recoverTrueMessage(byte[] encryptedZeroMessage,
      byte[] encryptedOneMessage, byte[] seed, boolean choiceBit) {
    if (encryptedZeroMessage.length != encryptedOneMessage.length) {
      throw new MaliciousException("The length of the two choice messages is not equal");
    }
    byte[] unpaddedMessage;
    if (choiceBit == false) {
      unpaddedMessage = PseudoOtp.decrypt(encryptedZeroMessage, seed);
    } else {
      unpaddedMessage = PseudoOtp.decrypt(encryptedOneMessage, seed);
    }
    return new StrictBitVector(unpaddedMessage);
  }


}
