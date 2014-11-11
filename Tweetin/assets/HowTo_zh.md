如何获取Key和Secret？
===

Twitter官方对第三方客户端的访问有频率限制，用户使用自己生成的API Key和API Secret则可以尽可能降低这种限制，获取更好的体验。于是我们决定不提供直接的帐号和密码登陆，而使用API认证。另一方面使用API认证也使得您的密码得到充分的保护。那么如何才能获取到Key和Secret呢？

 1. 前往[https://apps.twitter.com/](https://apps.twitter.com/ "Twitter Apps")并登陆。

 2. 点击__Create New App__按钮，在`Name`、`Description`、`Website`这几个文本输入框中随意填上一些内容。__注意不要在`Callback URL`这个文本输入框中输入任何内容！__接受《Developer Rules of the Road》协议并且点击__Create your Twitter application__按钮。

 3. 在__Permission__界面，将__Access__权限更改为`Read, Write and Access direct messages`，然后点击__Update Settings__按钮。

 4. 在__Key and Access Tokens__界面，你就可以看到已经配置好的`Consumer Key(API Key)`和`Consumer Secret(API Secret)`了，将它们复制粘贴到Tweetin的登陆界面相应的文本输入框中。

 5. 登陆过程中会弹出一个对话框，上面会显示__Pin__值，记下这个Pin值，登陆过程中的最后一步将会用到。

 6. 祝你拥有一段愉快的旅程 :)

## 注意：

如果在登陆过程中失败，请退出应用，稍等一会儿再重新打开并且尝试登陆，否则将一直登陆失败。
