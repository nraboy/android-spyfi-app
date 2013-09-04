// Spyfi
// Created by Nic Raboy

#import <UIKit/UIKit.h>

#import <CoreData/CoreData.h>

@interface InfoViewController : UITableViewController <NSFetchedResultsControllerDelegate>

@property (strong, nonatomic) NSManagedObjectContext *managedObjectContext;

- (IBAction)navigationCancel:(id)sender;
- (IBAction)navigationSave:(id)sender;

@property (weak, nonatomic) IBOutlet UITextField *cameraTitle;
@property (weak, nonatomic) IBOutlet UITextField *cameraHost;
@property (weak, nonatomic) IBOutlet UITextField *cameraPort;
@property (weak, nonatomic) IBOutlet UITextField *cameraUser;
@property (weak, nonatomic) IBOutlet UITextField *cameraPass;

@end
