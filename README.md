# StaffTicket
Allow's your users to submit tickets of bug reports, hacking, etc
## Features
- [ ] Submitting a basic ticket
- [ ] Opening a book and quill to write the message
- [ ] Storing messages in a database (h2/mysql/mongodb)
- [ ] Ability to escalate a ticket (helper -> mod -> admin)
- [ ] Logging of when tickets are sent / opened / resolved
- [ ] ability to assign someone to look at a ticket then when the login they'll get a notification
- [ ] scabale across multiple servers with bungee cord
## Permissions
- `staffticket.open` - Allows the opening of tickets
- `staffticket.review` - Allows the reviewing of tickets, seeing all tickets that aren't resolved
- `staffticket.close` - Closing the ticket marking it as resolved.
## Notes
- You can't delete a ticket, only marking them as resolved.
- All tickets will have a 8 character unique id: `H8AZ2E3U` 
