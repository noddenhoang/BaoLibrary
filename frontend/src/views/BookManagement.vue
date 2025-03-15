// Add this in the methods section:

checkPermission() {
  const hasPermission = apiService.debug.hasPermission(['admin', 'manager']);
  if (!hasPermission) {
    this.$toast.error('You need administrator or manager privileges to manage books');
    return false;
  }
  return true;
},

saveBook() {
  if (!this.checkPermission()) return;
  
  // Rest of the saveBook method...
},

deleteBook(book) {
  if (!this.checkPermission()) return;
  
  // Rest of the deleteBook method...
},

uploadImage(file) {
  if (!this.checkPermission()) return;
  
  // Rest of the uploadImage method...
}
