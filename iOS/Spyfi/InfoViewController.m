// Spyfi
// Created by Nic Raboy

#import "InfoViewController.h"
#import "AppDelegate.h"

@interface InfoViewController ()

@end

@implementation InfoViewController

@synthesize managedObjectContext;

- (void)viewDidLoad
{
    [super viewDidLoad];
    if (managedObjectContext == nil)
    {
        managedObjectContext = [(AppDelegate *)[[UIApplication sharedApplication] delegate] managedObjectContext];
    }
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}


- (IBAction)navigationCancel:(id)sender {
    [self dismissViewControllerAnimated:NO completion:nil];
}

- (IBAction)navigationSave:(id)sender {
    NSManagedObjectContext *context = [self managedObjectContext];
    NSManagedObject *managedObject = [NSEntityDescription insertNewObjectForEntityForName:@"Camera" inManagedObjectContext:context];
    NSNumberFormatter *f = [[NSNumberFormatter alloc] init];
    [f setNumberStyle:NSNumberFormatterDecimalStyle];
    NSNumber *myNumber = [f numberFromString:_cameraPort.text];
    [managedObject setValue:[_cameraTitle text] forKey:@"title"];
    [managedObject setValue:[_cameraHost text] forKey:@"host"];
    [managedObject setValue:myNumber forKey:@"port"];
    [managedObject setValue:[_cameraUser text] forKey:@"username"];
    [managedObject setValue:[_cameraPass text] forKey:@"password"];
    [self dismissViewControllerAnimated:NO completion:nil];
}

@end
