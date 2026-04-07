# Chat System - Quick Reference

## 📍 Chat Location
- **ONLY** appears when inside a Space view
- **RIGHT SIDE** of the space window (350px wide)
- **FULL HEIGHT** scrollable message area
- **NOT** visible on dashboard or any other screen

## 🎨 Design
- **Color Scheme**: Lavender, Purple, Glassmorphism
- **Sent Messages**: Right-aligned, purple bubbles
- **Received Messages**: Left-aligned, light lavender bubbles
- **Effects**: Subtle glow, transparent backgrounds, smooth shadows

## 📝 Files Modified/Created

| File | Purpose | Type |
|------|---------|------|
| `Message.java` | Message data model | NEW |
| `ChatController.java` | Chat UI logic & message handling | NEW |
| `space-with-chat.fxml` | Space + Chat layout | NEW |
| `space-chat.css` | Lavender theme styling | NEW |
| `DatabaseManager.java` | MongoDB message persistence | UPDATED |
| `DashboardController.java` | Integrated chat into space view | UPDATED |

## 🔧 How It Works

```
User opens a Space
    ↓
showSpaceDetails() loads space-with-chat.fxml
    ↓
ChatController loads messages from MongoDB
    ↓
Messages display with history
    ↓
User types & clicks Send
    ↓
Message saves to DB + appears in UI instantly
```

## 💾 Database

**MongoDB Collection**: `messages`

**Fields**:
- `id` (UUID string)
- `spaceId` (Space name - foreign key)
- `senderId` (Username)
- `messageText` (Message content)
- `timestamp` (ISO-8601 datetime)

**Behavior**:
- Messages linked to specific space
- Deleted when space is deleted
- Ordered by timestamp (oldest first)

## ✨ Key Features

✅ Real-time message display  
✅ Auto-scroll to latest message  
✅ Sender name & timestamp shown  
✅ Persistent storage in MongoDB  
✅ Space-specific isolation  
✅ Async message saving (non-blocking)  
✅ Glassmorphic UI with purple glow  
✅ Responsive layout  

## 🚀 Testing Steps

1. Create/open a space
2. Chat panel appears on right
3. Type message in input field
4. Click "Send"
5. Message appears as right-aligned purple bubble
6. Invite another user & send message
7. New user's message appears left-aligned
8. Close app & reopen → messages persist
9. Delete space → all messages deleted

## 🔑 Important Code Snippets

### Set Space in Controller
```java
ChatController chatCtrl = loader.getController();
chatCtrl.setSpace(space);
```

### Send Message
```java
Message msg = new Message(spaceId, userId, text);
DatabaseManager.saveMessage(msg);
```

### Load Messages
```java
List<Message> messages = DatabaseManager.getMessagesBySpaceId(spaceId);
```

### Delete Space Messages
```java
DatabaseManager.deleteMessagesBySpaceId(spaceId);
```

## 📊 MongoDB Queries

### View all messages in a space
```javascript
db.messages.find({ "spaceId": "Team Alpha" })
```

### Count messages
```javascript
db.messages.countDocuments({ "spaceId": "Team Alpha" })
```

### Delete all messages for a space
```javascript
db.messages.deleteMany({ "spaceId": "Team Alpha" })
```

### Create index for performance
```javascript
db.messages.createIndex({ "spaceId": 1, "timestamp": 1 })
```

## ⚙️ Configuration

**Chat Panel Width**: 350px (adjust in CSS if needed)

**Message Bubble Colors**:
- Sent: `rgba(147, 112, 219, 0.35)`
- Received: `rgba(230, 220, 250, 0.5)`

**Glow Effect**: `dropshadow(gaussian, rgba(147, 112, 219, 0.25), 20, 0.3)`

**Border Radius**: 20px (containers), 16px (bubbles)

## ❓ FAQ

**Q: Can I use chat outside a space?**  
A: No, chat is STRICTLY space-scoped. It only appears inside a space.

**Q: Will my messages disappear?**  
A: No, messages persist in MongoDB. They survive app restarts.

**Q: Can I see other users' messages?**  
A: Yes, if you're a member of the space. Non-members cannot access.

**Q: What happens when I delete a space?**  
A: All messages for that space are deleted from MongoDB.

**Q: Can I edit/delete my messages?**  
A: Not in this version. Future enhancement possible.

**Q: Are messages encrypted?**  
A: They're stored in MongoDB with standard encryption. Consider adding TLS for production.

## 🎓 Integration Points

The chat integrates at:
1. **DashboardController.showSpaceDetails()** - Loads FXML with chat
2. **ChatController** - Manages message display & sending
3. **DatabaseManager** - Persists messages to MongoDB
4. **Message Model** - Data transfer object

No other files are affected. Dashboard, Tasks, Focus Mode, etc. remain unchanged.

---

**Status**: ✅ Ready for Testing & Deployment
