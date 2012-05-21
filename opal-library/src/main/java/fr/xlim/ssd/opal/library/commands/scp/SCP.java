package fr.xlim.ssd.opal.library.commands.scp;

import fr.xlim.ssd.opal.library.commands.GP2xCommands;
import fr.xlim.ssd.opal.library.commands.SecLevel;
import fr.xlim.ssd.opal.library.config.SCGPKey;
import fr.xlim.ssd.opal.library.config.SCPMode;
import javax.smartcardio.CardException;
import javax.smartcardio.ResponseAPDU;

public interface SCP {

    SCPMode getScpMode();

    void setScpMode(SCPMode scpMode);

    SecLevel getSecMode();

    void setSecMode(SecLevel secMode);

    byte[] getIcv();

    void initIcv();

    byte[] getSessEnc();

    void setSessEnc(byte[] sessEnc);

    byte[] getSessMac();

    void setSessMac(byte[] sessMac);

    byte[] getSessRMac();

    void setSessRMac(byte[] sessRMac);

    byte[] getSessKek();

    void setSessKek(byte[] sessKek);

    byte[] getHostChallenge();

    void setHostChallenge(byte[] hostChallenge);

    byte[] getCardChallenge();

    void setCardChallenge(byte[] cardChallenge);

    byte[] getCardCrypto();

    void setCardCrypto(byte[] cardCrypto);

    byte[] getDerivationData();

    void setDerivationData(byte[] derivationData);

    byte[] getHostCrypto();

    void setHostCrypto(byte[] hostCrypto);

    byte[] getSequenceCounter();

    void setSequenceCounter(byte[] sequenceCounter);

    void calculateDerivationData();

    byte[] decryptCardResponseData(byte[] response) throws CardException;

    byte[] encryptCommand(byte[] command);

    byte[] generateMac(byte[] data);

    void initIcvToMacOverAid(byte[] aid);

    void calculateCryptograms();

    void generateSessionKeys(SCGPKey staticKenc, SCGPKey staticKmac, SCGPKey staticKkek);

    void detectAndInitSCP(byte keyId, SCPMode desiredScp, ResponseAPDU resp, GP2xCommands commands) throws SCPException;

    public void compudeAndVerifyRMac(byte[] response) throws SCPException;

    void setRENC_counter(int RENC_counter);

    int getCENC_Counter();

    void setCENC_Counter(int CENC_Counter);
}
