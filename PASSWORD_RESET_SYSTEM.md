# ✅ PASSWORD RESET SYSTEM - BASIC IMPLEMENTATION

## Overview

A basic, simple password reset system has been implemented for your Planora application. Users can reset their password through a forgot password flow with code verification.

## How It Works

### Flow
```
1. User clicks "Forgot password?" on Sign In page
   ↓
2. Forgot Password screen opens
   - User enters their email
   ↓
3. System generates a 6-digit reset code
   ↓
4. Code is displayed to user (in console for now)
   ↓
5. User enters the code
   ↓
6. Reset Password screen opens
   ↓
7. User sets new password
   ↓
8. Password updated in database
   ↓
9. Redirected to Sign In page
```

## Files Created

### 1. ForgotPasswordController.java
**Purpose**: Handles the forgot password flow
**Features**:
- Email validation
- 6-digit code generation
- Code verification
- Basic code "sending" (currently logs to console)

**Key Methods**:
- `handleSendCode()` - Validates email, generates code
- `handleVerifyCode()` - Verifies entered code
- `generateResetCode()` - Creates random 6-digit code
- `sendCodeToEmail()` - Currently logs to console

### 2. ResetPasswordController.java
**Purpose**: Handles password reset after code verification
**Features**:
- Password validation
- Password confirmation check
- Update password in database
- Redirect to login

**Key Methods**:
- `handleResetPassword()` - Validates and updates password
- `setUserEmail()` - Sets the user's email

### 3. UserManager.java (Updated)
**New Methods Added**:
```java
emailExists(String email)           // Check if email is registered
updatePassword(String email, String newPassword)  // Update by email
```

### 4. FXML Files Created
- `forgot-password.fxml` - UI for code entry
- `reset-password.fxml` - UI for new password entry

## Current Implementation (Basic)

### Code Sending
Currently, the system:
1. Generates a 6-digit code
2. **Displays it on screen** to the user (for testing)
3. Logs it to console

**Console Output**:
```
═══════════════════════════════════════
PASSWORD RESET CODE
═══════════════════════════════════════
Email: user@example.com
Code: 123456
═══════════════════════════════════════
```

## How to Upgrade to Real Email Sending

### Option 1: SendGrid (Easiest)

```java
// 1. Add dependency to pom.xml
<dependency>
    <groupId>com.sendgrid</groupId>
    <artifactId>sendgrid-java</artifactId>
    <version>4.9.1</version>
</dependency>

// 2. Create SendEmailUtil class
public class SendEmailUtil {
    private static final String SENDGRID_API_KEY = "your_api_key";
    
    public static void sendResetCode(String email, String code) throws Exception {
        Email from = new Email("noreply@planora.com");
        Email to = new Email(email);
        Content content = new Content("text/plain", 
            "Your password reset code is: " + code);
        Mail mail = new Mail(from, "Password Reset", to, content);
        
        SendGrid sg = new SendGrid(SENDGRID_API_KEY);
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());
        sg.api(request);
    }
}

// 3. Update ForgotPasswordController
private void sendCodeToEmail(String email, String code) {
    try {
        SendEmailUtil.sendResetCode(email, code);
    } catch (Exception e) {
        System.err.println("Failed to send email: " + e.getMessage());
    }
}
```

### Option 2: Gmail SMTP

```java
// 1. Create GmailSender class
public class GmailSender {
    public static void sendResetCode(String recipientEmail, String code) throws Exception {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("your_email@gmail.com", "your_password");
            }
        });
        
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("your_email@gmail.com"));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
        message.setSubject("Planora Password Reset Code");
        message.setText("Your password reset code is: " + code);
        
        Transport.send(message);
    }
}

// 2. Update ForgotPasswordController
private void sendCodeToEmail(String email, String code) {
    try {
        GmailSender.sendResetCode(email, code);
    } catch (Exception e) {
        System.err.println("Failed to send email: " + e.getMessage());
    }
}
```

### Option 3: Firebase Cloud Messaging

```java
// Send code via Firebase for in-app notifications
public class FirebaseEmailSender {
    public static void sendResetCode(String email, String code) {
        // Implement Firebase Cloud Messaging
    }
}
```

## Testing the System

### Test Flow
1. Go to Sign In page
2. Click "Forgot password?"
3. Enter a registered email
4. Check console for code
5. Enter code on screen
6. Set new password
7. Get redirected to Sign In

### Test Cases
- ✅ Invalid email format
- ✅ Non-existent email
- ✅ Wrong verification code
- ✅ Password too short
- ✅ Password mismatch
- ✅ Successful reset

## Security Considerations

### Current (Basic)
- Code displayed on screen (for development)
- Code logged to console
- 6-digit random code

### For Production
1. **Generate stronger codes**: Use 8+ characters
2. **Use HTTPS**: All communications encrypted
3. **Add expiration**: Codes valid for 15 minutes
4. **Rate limiting**: Limit code generation attempts
5. **Secure email**: Use proper email service
6. **Hash passwords**: Already stored properly
7. **Add audit logging**: Track password reset attempts
8. **CSRF protection**: Add token validation

## Code Structure

```
User clicks "Forgot password?" (Sign In page)
    ↓
SignInController.handleForgotPassword()
    ↓
Load forgot-password.fxml with ForgotPasswordController
    ↓
User enters email → handleSendCode()
    ↓
generateResetCode() → sendCodeToEmail()
    ↓
Display code on screen + console
    ↓
User enters code → handleVerifyCode()
    ↓
Load reset-password.fxml with ResetPasswordController
    ↓
User enters new password → handleResetPassword()
    ↓
UserManager.updatePassword(email, newPassword)
    ↓
Update MongoDB → goToSignIn()
    ↓
Redirect to Sign In page
```

## Database Changes

**User Document** (No changes needed - email already stored):
```json
{
  "username": "john_doe",
  "password": "hashed_password",
  "email": "john@example.com",
  "created_at": "2026-04-07T..."
}
```

## API Reference

### ForgotPasswordController

```java
public void handleSendCode()
// Validates email, generates code, sends it

public void handleVerifyCode()
// Verifies code entered by user

public String generateResetCode()
// Returns 6-digit code

private void sendCodeToEmail(String email, String code)
// Currently: logs to console
// TODO: Integrate with email service
```

### ResetPasswordController

```java
public void setUserEmail(String email)
// Set the email for password reset

public void handleResetPassword()
// Validates and updates password
```

### UserManager (New Methods)

```java
public static boolean emailExists(String email)
// Check if email is in system

public static boolean updatePassword(String email, String newPassword)
// Update password by email
```

## Current vs Production Ready

### Current State ✓
- ✅ Basic functionality working
- ✅ Code generation working
- ✅ Code verification working
- ✅ Password update working
- ✅ Database integration done
- ✅ UI complete

### What's Needed for Production 🔧
- Email service integration
- Code expiration (15 min)
- Rate limiting
- Stronger codes (8+ chars)
- Audit logging
- HTTPS enforcement
- CSRF protection

## Summary

Your password reset system is now:
- ✅ **Basic & Simple** - Easy to understand and modify
- ✅ **Functional** - All features work as intended
- ✅ **Ready to Extend** - Easy to add real email sending
- ✅ **Secure Enough** - For development/testing
- ⚠️ **Not Production Ready** - Needs email service integration

To add real email sending, follow one of the upgrade options above (SendGrid recommended for simplicity).

**Status**: ✅ COMPLETE - Ready for development/testing
