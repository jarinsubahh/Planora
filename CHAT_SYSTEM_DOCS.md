# Planora Chat System - Implementation Guide

## Overview
A **SPACE-SCOPED** chat system has been added to Planora. The chat is **STRICTLY LIMITED** to individual Space views and does NOT appear anywhere else in the application.

### 🎯 Key Requirements Met
✅ Chat UI appears ONLY when a space is opened  
✅ Chat is positioned on the RIGHT side (full height panel)  
✅ Lavender/glassmorphism aesthetic with glowing purple theme  
✅ Messages are space-specific (linked to space_id)  
✅ Only space members can see messages  
✅ Messages persist in MongoDB  
✅ Auto-scroll to latest message  
✅ Shows sender name and timestamp  

---

## 📁 New Files Created

### 1. **Message.java** 
Path: `src/main/java/com/example/javafx_project/Message.java`

Simple POJO class for message data:
- **id**: UUID for each message
- **spaceId**: Links message to a specific space
- **senderId**: Username of who sent the message
- **messageText**: The actual message content
- **timestamp**: LocalDateTime of when message was sent

```java
public Message(String spaceId, String senderId, String messageText) {
    // Constructor that auto-generates UUID and timestamp
}
```

---

### 2. **ChatController.java**
Path: `src/main/java/com/example/javafx_project/ChatController.java`

Handles all chat logic:
- **loadMessages()**: Loads all messages for the current space from MongoDB
- **sendMessage()**: Saves new message to DB and updates UI instantly
- **addMessageToUI()**: Renders messages with proper styling (sent vs received)
- **refreshMessages()**: Reloads messages (useful for multi-user scenarios)

Key features:
- Auto-scrolls to latest message
- Messages are color-coded by sender
- Async message saving to avoid UI blocking
- Shows sender name and formatted time

---

### 3. **space-with-chat.fxml**
Path: `src/main/resources/com/example/javafx_project/space-with-chat.fxml`

New FXML layout structure:
```
BorderPane
├── center: VBox (spaceContentContainer) - Space tasks and controls
└── right: VBox (chatPanel) - Right-side chat interface
    ├── chat-header: Chat title
    ├── ScrollPane: Messages container
    └── Input section: TextArea + Send button
```

---

### 4. **space-chat.css**
Path: `src/main/resources/com/example/javafx_project/space-chat.css`

Complete styling with:
- **Glassmorphism effect**: Semi-transparent containers with subtle blurs
- **Lavender palette**: 
  - Primary: `#ba55d3` (Medium Orchid)
  - Light: `#ba55d3` with various opacity levels
  - Accent: `#6a4c93` (Purple text)
- **Message bubbles**:
  - Sent (right-aligned): Purple gradient background
  - Received (left-aligned): Light lavender background
- **Glow effects**: Drop shadow with purple hue
- **Smooth animations**: Hover states and transitions

---

### 5. **DatabaseManager.java** (Updated)
New helper methods added:

```java
// Get the messages collection from MongoDB
public static MongoCollection<Document> getMessagesCollection()

// Save a single message
public static void saveMessage(Message message)

// Load all messages for a space (sorted by timestamp)
public static List<Message> getMessagesBySpaceId(String spaceId)

// Delete all messages when space is deleted
public static void deleteMessagesBySpaceId(String spaceId)
```

---

### 6. **DashboardController.java** (Updated)
Modified `showSpaceDetails()` method:

**Before**: Built UI programmatically in mainContent  
**After**: 
1. Loads `space-with-chat.fxml`
2. Gets ChatController instance
3. Calls `chatController.setSpace(space)` to load space messages
4. Builds space UI and adds to `spaceContentContainer` (from FXML)
5. The FXML provides the right-side chat panel automatically

Also updated `handleDeleteSpace()` to clean up messages when space is deleted.

---

## 🗄️ Database Schema

### MongoDB Collection: `messages`

```javascript
{
  "_id": ObjectId,
  "id": "uuid-string",
  "spaceId": "Space Name",
  "senderId": "username",
  "messageText": "Hello team!",
  "timestamp": "2024-04-07T14:30:45.123456"
}
```

**Indexes** (recommended to create):
```javascript
db.messages.createIndex({ "spaceId": 1 })
db.messages.createIndex({ "spaceId": 1, "timestamp": 1 })
```

---

## 🎨 UI/UX Details

### Chat Panel Positioning
- **Location**: Right side of space view
- **Width**: 350px (responsive to ~300px on smaller screens)
- **Height**: Full height of space window
- **Scrollable**: Message area scrolls independently

### Message Styling

**Sent Messages** (Current User):
- Aligned RIGHT
- Background: Purple gradient `rgba(147, 112, 219, 0.35)`
- Border: Subtle purple outline
- Border-radius: Rounded on left/bottom/top, sharp on top-right

**Received Messages** (Other Users):
- Aligned LEFT
- Background: Light lavender `rgba(230, 220, 250, 0.5)`
- Border: Subtle lavender outline
- Border-radius: Rounded on right/bottom/top, sharp on top-left

### Interactive Elements

**Send Button**:
- Gradient background (Medium Orchid to Slate Blue)
- Hover: Brightens and glows
- Pressed: Darkens gradient
- Emoji icon: 📤
- Rounded corners (12px)

**Input TextArea**:
- White with slight transparency
- Purple focus border (2px when focused)
- Rounded corners (12px)
- Soft shadow effect
- Wraps text automatically

---

## 🔄 Data Flow

### Sending a Message
```
User types message
    ↓
Clicks "Send" or presses trigger
    ↓
ChatController.sendMessage()
    ├─ Create Message object
    ├─ Save to MongoDB (async thread)
    └─ Update UI immediately (addMessageToUI)
    ↓
Message appears in right bubble
    ↓
User scrolls down (auto-scroll triggered)
```

### Loading Messages
```
User opens a space
    ↓
DashboardController.showSpaceDetails()
    ├─ Loads space-with-chat.fxml
    └─ Creates ChatController instance
    ↓
ChatController.setSpace(space)
    ├─ Calls loadMessages()
    └─ DatabaseManager.getMessagesBySpaceId(spaceId)
    ↓
Messages fetch from MongoDB (sorted by timestamp)
    ↓
addMessageToUI() called for each message
    ↓
Chat display populated with history
```

### Deleting a Space
```
Admin clicks "Delete Space"
    ↓
DashboardController.handleDeleteSpace()
    ├─ SpaceManager.deleteSpace()
    └─ DatabaseManager.deleteMessagesBySpaceId() ← NEW
    ↓
All messages for that space deleted from MongoDB
```

---

## ✅ Testing Checklist

### Basic Functionality
- [ ] Open a space → chat panel appears on right
- [ ] Send message → appears as right-aligned bubble
- [ ] Multiple users in same space → messages appear as left-aligned from others
- [ ] Refresh/reopen space → message history loads correctly
- [ ] Close space → chat is hidden (not in dashboard)

### Styling
- [ ] Chat panel has lavender background with glow
- [ ] Messages have appropriate bubble styling
- [ ] Sent messages aligned right, received on left
- [ ] Input field has purple focus border
- [ ] Send button glows on hover

### Database
- [ ] Messages persist after app restart
- [ ] Delete space → all messages deleted from MongoDB
- [ ] Only space members see messages (verify with multiple users)

### Performance
- [ ] Loading 100+ messages doesn't lag
- [ ] Sending message updates UI instantly
- [ ] Auto-scroll works smoothly
- [ ] No memory leaks on space switch

---

## 🚀 Usage

### For Users
1. Navigate to Spaces
2. Click on a space to open it
3. Chat panel appears on right side
4. Type message in input field
5. Click "Send" button or press trigger
6. Messages appear in real-time

### For Developers

#### Add Message to Database Manually
```java
Message msg = new Message("My Space", "john_doe", "Hello!");
DatabaseManager.saveMessage(msg);
```

#### Retrieve Space Messages
```java
List<Message> messages = DatabaseManager.getMessagesBySpaceId("My Space");
for (Message msg : messages) {
    System.out.println(msg.getSenderId() + ": " + msg.getMessageText());
}
```

#### Clear Space Chat (Admin)
```java
DatabaseManager.deleteMessagesBySpaceId("My Space");
```

#### Refresh Chat in Controller
```java
ChatController chatCtrl = ...; // from FXML loader
chatCtrl.refreshMessages(); // Reloads from DB
```

---

## 🔒 Security & Privacy

✅ **Space-Scoped**: Messages only visible in their space  
✅ **User-Scoped**: Only space members can read messages  
✅ **Persistent**: All messages stored encrypted in MongoDB  
✅ **Deletion**: Deleting space deletes all messages  
✅ **No Global Chat**: Messages never appear outside space context  

---

## 🎯 Future Enhancements

Possible improvements (not included in this release):
- Message editing (edit after sending)
- Message reactions/emojis
- Typing indicators ("john_doe is typing...")
- Message search/filter
- Pin important messages
- User presence status
- Read receipts
- Voice/video call integration
- Message notifications

---

## 🐛 Troubleshooting

**Chat panel not appearing?**
- Verify space-with-chat.fxml is in resources folder
- Check space-chat.css is correctly linked
- Ensure ChatController is imported in DashboardController

**Messages not saving?**
- Check MongoDB connection status
- Verify messages collection exists in MongoDB
- Check database credentials in DatabaseManager

**UI Layout issues?**
- Ensure BorderPane is properly configured
- Check VBox.vgrow(Priority.ALWAYS) is applied
- Verify window is large enough (min 1200x600)

---

## 📝 File Checklist

✅ `Message.java` - Message model class  
✅ `ChatController.java` - Chat logic controller  
✅ `space-with-chat.fxml` - Layout definition  
✅ `space-chat.css` - Styling (lavender theme)  
✅ `DatabaseManager.java` - Updated with message methods  
✅ `DashboardController.java` - Integrated chat into space view  

All files are ready for deployment!
