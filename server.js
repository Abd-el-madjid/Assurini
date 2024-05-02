const express = require("express");
const admin = require("firebase-admin");
const nodemailer = require("nodemailer");
// node server.js
// Initialize Firebase Admin SDK
const serviceAccount = {
  type: "service_account",
  project_id: "assurini-ec942",
  private_key_id: "11255b9069ea79e7a3b6405bd72835290061fd3c",
  private_key:
    "-----BEGIN PRIVATE KEY-----\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDl4zAB6fl2iGJ3\nhlEd952L2CKaKa+zxVP8RwOaRXvzQoXH5FXkRJJ0yFo+dotLPg4VaaDKq4XPcEBl\ngKkt9Yn3QICdoiM/uzB+0BTMyRcmWGQRpIM5smGixAzRPj2wvvTWp+SAGsXb8rY4\nb27vON5Epp36j33DsjOIBzwiC4vbYiXjLPrYFlF8N7Oklzqz6fifhLYLWkK+4rx+\nGBqwPWUzmvsqbBmnni9VIcVuNRGwEajAnshuiN/p7RlOS5Y3E0QPhVygfp4TK1Xw\n9Hjlsr6cnuzQTDu41MiPgs+HI/oDSb9WxNO7Xukuy9m3KKIpyj+qxkFfLOOc6k4Q\ncGHD3qllAgMBAAECggEAItKDYOLXgCpAGiFdM7ggpACJG0eRvx42rtn19H4ufKZ4\nu9L9ZL6zXZ/Etb4AsxTTDtDJg7GwKwAyrP1sgxL0w2CAqwAWrU52VdtAr1WNnWgZ\nvmdoXkrESeK/G0n9j8KZNVPIw9PqYTsWeZ19lRyM5KLFfuER+J0Le8E2zWRo2sHt\nd+isQTCP0KS2pM0VSWDC9Ml3LgA2xWHlVbJKusqMNYYeQ9PS3crhMwMiZaB90kxq\nYRSxbAM0NiheeRdUuw1hYw3Am1J3GpVNwH7vNgiL9Zl57uC2KELg7X53LPK/Rj6x\n7E7z9mZa3rOHFXJ0ajy8P4u3QeHlhPkVsabkulH+iQKBgQD7HDRbHwMuCQ/OwAGo\n3UzOJChp0sbC6oUlqBDpnIDa1yxFNhYoSE78ssgD0CBWmniZtqIdPH1ZuhS+h/GG\nMHYl2GRaHMKlgh1HC/Xvv1NSERNQf/QoX6u8cB8iXTDbW6SG26pLrR+jaBNVZpAr\nNs1ArsKNcMaNlhWTZx8ryxZ4DQKBgQDqXS/RbPemFaSOj+RURyML7BzG+9e4Sf3O\n9dwQWBKryvgS6eimZfZxihUSyKCAPhJl0iK34LA1/1l2m2ul3HygkSMRleSxBiv0\n6y2yKNKG9EvX7PfAY+gJ0wj20mpoT5gq8uCbA2ok2bjxAp6KivUKenv36vCztBoF\nx/G3SoeIuQKBgEfyD8ofu5CABihteMxCA5R4ZZ/URSBpgxaD7byRG5YX2lZGJdkK\nxp6+NnJpOiq1/WHFIvOPdja0CueAGaIh1mgHdNEgtmAnRG4rrh4vyhaUT91bm7/J\nkXHfCfyViL8ApcYBv+wIwSzXT9rDFQFcpTgoGhPeQPUmEC54eETa3igtAoGAMoau\nlXbpmSvXU0jgT+8aNirMNXX8FuYUyvakX/2s4M6cVu+I0b7vt6CuVGNefudi7gK0\nq3HMa4fGkNSHC7YSREemCNeI+0U6Ws4NBvjYEnI+m6mh7B9d2iheKWBJwn1OcAv2\nslS1IIaz4XnQFkxI8d5gNZ24EPYGGjL2AK8XszkCgYEAgkDY8khCAgzfOJRGMN9J\nzMROWHlQFG681RDzL5YIqhsBudV3JeHJ3WK16tgEBuKX3u0m+eE6fUv1SpiRoI1M\nyH61WGyen0vr7NOGQY/sRoj0d3swiQc6kBZcynhn/BawWpfwx1an0fZFsY9BKc2o\ncmpVnrSOR1LmAJJbBjsvlYs=\n-----END PRIVATE KEY-----\n",
  client_email:
    "firebase-adminsdk-4pvkv@assurini-ec942.iam.gserviceaccount.com",
  client_id: "105510656050686205735",
  auth_uri: "https://accounts.google.com/o/oauth2/auth",
  token_uri: "https://oauth2.googleapis.com/token",
  auth_provider_x509_cert_url: "https://www.googleapis.com/oauth2/v1/certs",
  client_x509_cert_url:
    "https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-4pvkv%40assurini-ec942.iam.gserviceaccount.com",
  universe_domain: "googleapis.com",
};
admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: "https://assurini-ec942-default-rtdb.firebaseio.com/", // Update with your Firebase database URL
});

const app = express();
const port = 3000;

// Reference to the Notification table
const notificationRef = admin.database().ref("/Notification");
// Setup nodemailer transporter
const transporter = nodemailer.createTransport({
  service: "gmail",
  auth: {
    user: "assurini.noreply@gmail.com", // Replace with your Gmail email
    pass: "jdjhtgvcxwfvlgoi",
  },
});

// Listen for changes in the Notification table
notificationRef.on("child_added", async (snapshot) => {
  try {
    const notification = snapshot.val();

    if (!notification || !notification.idPersonne) {
      console.error("Invalid notification data");
      return;
    }

    // Fetch the user's FCM token from the Personne table based on the idPersonne
    const snapshotPersonne = await admin
      .database()
      .ref(`/Personne/${notification.idPersonne}`)
      .once("value");
    const user = snapshotPersonne.val();

    if (!user || !user.token || !user.email) {
      console.error("User not found or FCM token or email not available");
      return;
    }

    const message = {
      notification: {
        title: notification.title || "Default Title",
        body: notification.contenu || "Default Body",
      },
      token: user.token,
    };

    // Send the notification
    const response = await admin.messaging().send(message);

    console.log("Successfully sent message:", response);

    // Send email
    const mailOptions = {
      from: "assurini.noreply@gmail.com", // Replace with your Gmail email
      to: user.email,
      subject: notification.title || "Default Email Title",
      text: notification.contenu || "Default Email Body",
    };

    // Send the email
    const emailResponse = await transporter.sendMail(mailOptions);

    console.log("Successfully sent email:", emailResponse);
  } catch (error) {
    console.error("Error:", error);
  }
});

app.listen(port, () => {
  console.log(`Server is running at http://localhost:${port}`);
});
